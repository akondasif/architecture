package com.rainyalley.architecture.service;


import com.rainyalley.architecture.core.Page;

import java.util.List;

/**
 * 事务顶层接口
 *
 * @param <T>
 */
public interface Service<T> {
    /**
     * 保存一个对象
     *
     * @param obj 将被添加的对象
     * @return 受影响的对象个数
     */
    T save(T obj);

    /**
     * 删除一个对象
     *
     * @param obj 将被删除的对象
     * @return 受影响的对象个数
     */
    int remove(T obj);

    /**
     * 获取一个对象
     *
     * @param obj 存放查询信息的对象
     * @return 获取的对象
     */
    T get(T obj);

    /**
     * 获取一组对象
     *
     * @param obj  存放查询信息的对象
     * @param page 分页信息
     * @return 对象列表
     */
    List<T> get(T obj, Page page);
}
