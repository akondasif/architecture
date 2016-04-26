package com.rainyalley.architecture.common.aop; 

import com.rainyalley.architecture.common.user.model.entity.User;
import com.rainyalley.architecture.common.user.service.UserService;
import com.rainyalley.architecture.core.Page;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/** 
* InvokeCacheAspect Tester. 
*/ 
@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations = "classpath:spring-common.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class InvokeCacheAspectTest {

    @Autowired
    private InvokeCacheAspect invokeCacheAspect;

    @Autowired
    private UserService userService;



    @Before
    public void before() throws Exception { 
    } 

    @After
    public void after() throws Exception { 
    } 

    /** 
    * 
    * Method: around(ProceedingJoinPoint joinPoint) 
    * 
    */ 
    @Test
    public void testAround() throws Exception {
        userService.get(new User(), new Page());
    } 

    /** 
    * 
    * Method: setCacheProvider(CacheProvider cacheProvider) 
    * 
    */ 
    @Test
    public void testSetCacheProvider() throws Exception { 
        //TODO: Test goes here... 
    } 

    /** 
    * 
    * Method: put(String key, Object value) 
    * 
    */ 
    @Test
    public void testPut() throws Exception { 
        //TODO: Test goes here... 
    } 

    /** 
    * 
    * Method: get(String key, Class<V> type) 
    * 
    */ 
    @Test
    public void testGet() throws Exception { 
        //TODO: Test goes here... 
    } 









// private methods ~~~~







    /** 
    * 
    * Method: extraCacheKey(PointContext context) 
    * 
    */ 
    @Test
    public void testExtraCacheKey() throws Exception { 
        //TODO: Test goes here... 
                /* 
                try { 
                   Method method = InvokeCacheAspect.getClass().getMethod("extraCacheKey", PointContext.class); 
                   method.setAccessible(true); 
                   method.invoke(<Object>, <Parameters>); 
                } catch(NoSuchMethodException e) { 
                } catch(IllegalAccessException e) { 
                } catch(InvocationTargetException e) { 
                } 
                */ 
            } 

    /** 
    * 
    * Method: doPut(String key, Object value) 
    * 
    */ 
    @Test
    public void testDoPut() throws Exception { 
        //TODO: Test goes here... 
                /* 
                try { 
                   Method method = InvokeCacheAspect.getClass().getMethod("doPut", String.class, Object.class); 
                   method.setAccessible(true); 
                   method.invoke(<Object>, <Parameters>); 
                } catch(NoSuchMethodException e) { 
                } catch(IllegalAccessException e) { 
                } catch(InvocationTargetException e) { 
                } 
                */ 
            } 

    /** 
    * 
    * Method: doGet(String key, Class<V> type) 
    * 
    */ 
    @Test
    public void testDoGet() throws Exception { 
        //TODO: Test goes here... 
                /* 
                try { 
                   Method method = InvokeCacheAspect.getClass().getMethod("doGet", String.class, Class<V>.class); 
                   method.setAccessible(true); 
                   method.invoke(<Object>, <Parameters>); 
                } catch(NoSuchMethodException e) { 
                } catch(IllegalAccessException e) { 
                } catch(InvocationTargetException e) { 
                } 
                */ 
            } 

} 
