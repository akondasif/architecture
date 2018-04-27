package com.rainyalley.architecture.arithmetic.sort;

import com.rainyalley.architecture.util.Chunk;
import com.rainyalley.architecture.util.QueuedLineReader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bin.zhang
 */
public class FileSorter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSorter.class);

    /**
     * 初始化块级别
     */
    private static final int INITIAL_CHUNK_LEVEL = 1;

    /**
     * 第一行的行号
     */
    private static final int INITIAL_ROW_NUM = 1;

    /**
     * io缓冲大小
     */
    private int ioBufferSize = 1024 * 1024 * 2;

    /**
     * 临时目录
     */
    private File tmpDir;

    /**
     * 行内容比较器
     */
    private Comparator<String> comparator;

    /**
     * 归并路数，必须大于或等于2
     */
    private int mergeWayNum = 8;

    /**
     * 每个块的行数
     */
    private int initialChunkSize = 1;

    /**
     * 排序完成后是否删除临时文件
     */
    private boolean clean;

    /**
     * 0-4个线程活动
     * 阻塞队列容量100
     * 最大空闲时间10秒
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     *
     * @param comparator 行比较器
     * @param mergeWayNum 归并路数
     * @param initialChunkSize 初始块中的行数
     * @param ioBufferSize io缓冲区容量
     * @param tmpDir 临时目录
     * @param clean 排序完成后，是否清除临时文件
     */
    public FileSorter(Comparator<String> comparator, int mergeWayNum, int initialChunkSize, int ioBufferSize, File tmpDir, boolean clean) {
        Assert.isTrue(mergeWayNum >=2, "mergeWayNum must greater than 2");
        Assert.isTrue(initialChunkSize >=1, "initialChunkSize must greater than 1");
        Assert.isTrue(ioBufferSize >=1024, "ioBufferSize must greater than 1024");
        Assert.notNull(tmpDir, "tmpDir can not be null");

        this.comparator = comparator;
        if(!tmpDir.exists()){
            tmpDir.mkdirs();
        }

        this.mergeWayNum = mergeWayNum;
        this.initialChunkSize = initialChunkSize;
        this.ioBufferSize = ioBufferSize;
        this.tmpDir = tmpDir;
        this.clean = clean;

        //并发归并线程池，队列容量为归并路数
        this.threadPoolExecutor = new ThreadPoolExecutor(8, 8, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(8),
                new CustomizableThreadFactory("fileSorterTPE-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    /**
     * 排序
     * @param original 原文件
     * @param dest 排序后的文件
     * @throws IOException
     */
    public void sort(File original, File dest) throws IOException {
        beforeSort(original, dest);

        List<Chunk> splitChunkList = split(original);

        //块队列，将从此队列中不断取出块，合并后放入此队列
        Queue<Chunk> chunkQueue = new LinkedList<>(splitChunkList);

        //当前处理的chunk level
        int currentLevel = INITIAL_CHUNK_LEVEL;

        List<Future<Chunk>> mergeFutureList = new ArrayList<>();
        while (true) {
            //从队列中获取一组chunk
            List<Chunk> pollChunks = pollChunks(chunkQueue, currentLevel);

            //未取到同级chunk, 表示此级别应合并完成
            if (CollectionUtils.isEmpty(pollChunks)) {
                mergeFutureList.stream().map(this::get).forEach(chunkQueue::add);
                mergeFutureList.clear();
                //chunkQueue 中只有一个元素，表示此次合并是最终合并
                if (chunkQueue.size() == 1) {
                    break;
                } else {
                    currentLevel++;
                    continue;
                }
            }

            Future<Chunk> chunk = threadPoolExecutor.submit(() -> merge(pollChunks, original));
            mergeFutureList.add(chunk);
        }

        Chunk finalChunk = chunkQueue.poll();
        File finalChunkFile = chunkFile(finalChunk, original);
        boolean renamed = finalChunkFile.renameTo(dest);
        if (!renamed) {
            throw new IllegalStateException(String.format("rename failure: %s >>> %s", finalChunkFile.getPath(), dest.getPath()));
        }

        afterSort(original, dest);
    }

    private Chunk get(Future<Chunk> future){
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void beforeSort(File original, File dest) throws IOException{
        Assert.isTrue(original.exists(), String.format("original file not fond: %s", original.getPath()));
        Assert.isTrue(!dest.exists(), String.format("dest file must not exist: %s", dest.getPath()));

        File workDir = workDir(original);
        if(!workDir.exists()){
            workDir.mkdir();
        } else {
            FileUtils.cleanDirectory(workDir);
        }
    }

    private void afterSort(File original, File dest) throws IOException{
        if(clean){
            FileUtils.deleteDirectory(workDir(original));
        }
    }

    /**
     * 合并
     * @param chunks
     * @param originalFile
     * @return
     * @throws IOException
     */
    private Chunk merge(List<Chunk> chunks, File originalFile) throws IOException{

        Stream<Chunk> cs = chunks.stream();
        Integer mergeSize = cs.map(Chunk::getSize).reduce((p, n) -> p + n).get();
        Chunk mergedChunk = new Chunk(chunks.get(0).getLevel() + 1, chunks.get(0).getIdx(), mergeSize);
        File mergedChunkFile = chunkFile(mergedChunk, originalFile);

        try(BufferedWriter mergedChunkFileWriter = new BufferedWriter(new FileWriter(mergedChunkFile), ioBufferSize)){
            List<QueuedLineReader> qlrList = chunks.stream().map(c -> createQueuedLineReader(c, originalFile)).collect(Collectors.toList());

            while (true){
                QueuedLineReader minReader = peekMinRemoveNull(qlrList);
                //所有reader都读完了
                if(minReader == null){
                    break;
                }

                String line = minReader.take();
                mergedChunkFileWriter.write(line);
                mergedChunkFileWriter.newLine();
            }
        }


        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("merge size {}, {}", chunks.size(), chunks);
        }
        return mergedChunk;
    }

    private QueuedLineReader createQueuedLineReader(Chunk chunk, File originalFile){
        try {
            return new QueuedLineReader(new BufferedReader(new FileReader(chunkFile(chunk, originalFile)), ioBufferSize));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private QueuedLineReader peekMinRemoveNull(List<QueuedLineReader> list){
        Iterator<QueuedLineReader> i = list.iterator();
        QueuedLineReader candidate = null;

        while (i.hasNext()) {
            QueuedLineReader next = i.next();

            if(next.peek() == null){
                next.close();
                i.remove();
                continue;
            } else if(candidate == null){
                candidate = next;
                continue;
            }

            if (comparator.compare(next.peek(), candidate.peek()) < 0){
                candidate = next;
            }
        }
        return candidate;
    }


    /**
     * 弹出chunk列表
     * @param chunkQueue 全局chunkQueue
     * @param level 弹出的trunk级别
     * @return
     */
    private List<Chunk> pollChunks(Queue<Chunk> chunkQueue, int level){
        List<Chunk> pollChunks = new ArrayList<>(mergeWayNum);
        for (int i = 0; i < mergeWayNum; i++) {
            Chunk head = chunkQueue.peek();
            if(head == null){
                break;
            }
            if(head.getLevel() == level){
                pollChunks.add(chunkQueue.poll());
            } else if(head.getLevel() > level){
                break;
            }
        }
        return pollChunks;
    }

    /**
     * 切割
     * @param file 被切割的文件
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<Chunk> split(File file) throws IOException{

        List<Chunk> chunkList = Collections.emptyList();

        try(BufferedReader br = new BufferedReader(new FileReader(file), ioBufferSize)){
            int rowNum = INITIAL_ROW_NUM;
            String line = null;
            List<String> chunkRows = new ArrayList<>(initialChunkSize);

            List<Future<Chunk>> splitFutureList = new ArrayList<>();
            while (true){
                line = br.readLine();

                if(line != null){
                    chunkRows.add(line);
                }

                if(line == null || chunkRows.size() >= initialChunkSize){
                    if(chunkRows.size() > 0){
                        final int rn = rowNum;
                        final List<String> cr = chunkRows;

                        rowNum += chunkRows.size();
                        chunkRows = new ArrayList<>();

                        Future<Chunk> chunk = threadPoolExecutor.submit(() -> {
                            cr.sort(comparator);
                            return initialChunk(rn, cr, file);
                        });

                        splitFutureList.add(chunk);
                    }
                }

                if(line == null){
                    break;
                }
            }
            chunkList = splitFutureList.stream().map(this::get).collect(Collectors.toList());
        }
        return chunkList;
    }

    /**
     * 创建一个块
     * @param rowNum 块首行的行号
     * @param chunkRows 块中的行数
     * @param originalFile 原始文件
     * @return
     * @throws IOException
     */
    private Chunk initialChunk(int rowNum, List<String> chunkRows, File originalFile) throws IOException{
        Chunk chunk = new Chunk(INITIAL_CHUNK_LEVEL, rowNum, chunkRows.size());
        File chunkFile = chunkFile(chunk, originalFile);
        if(chunkFile.exists()){
            FileUtils.deleteQuietly(chunkFile);
        }
        Assert.isTrue(chunkFile.createNewFile(), String.format("createNewFile failure: %s", chunkFile.getPath()));
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(chunkFile), ioBufferSize)){
            for (String chunkRow : chunkRows) {
                bw.write(chunkRow);
                bw.newLine();
            }
        }
        return chunk;
    }

    private File chunkFile(Chunk chunk, File originalFile){
        return new File(String.format(workDir(originalFile).getPath() + File.separator +"%s_%s_%s.txt", chunk.getLevel(), chunk.getIdx(), chunk.getSize()));
    }

    private File workDir(File originalFile){
        return new File(tmpDir.getPath() + File.separator + originalFile.getName());
    }

}