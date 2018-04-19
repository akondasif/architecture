package com.rainyalley.architecture.service.impl;


import com.rainyalley.architecture.dao.BaseMapper;
import com.rainyalley.architecture.dao.entity.UserDo;
import com.rainyalley.architecture.dao.mapper.UserMapper;
import com.rainyalley.architecture.service.UserService;
import com.rainyalley.architecture.service.aop.Secondary;
import com.rainyalley.architecture.service.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@CacheConfig(cacheNames = "user")
@Service
public class UserServiceImpl extends ServiceBasicSupport<User,UserDo> implements UserService {

    @Resource
    private UserMapper userMapper;


    @CachePut(key = "#result.id",condition = "#result != null")
    @Override
    @Transactional
    public User save(User obj) {
        return super.save(obj);
    }

    @Override
    protected BaseMapper<UserDo> getDao() {
        return userMapper;
    }

    @Override
    protected UserDo toDo(User b) {
        UserDo d  = new UserDo();
        d.setId(b.getId());
        d.setName(b.getName());
        d.setPassword(b.getPassword());
        return d;
    }

    @Override
    protected User toBo(UserDo d) {
        User user  = new User();
        user.setId(d.getId());
        user.setName(d.getName());
        user.setPassword(d.getPassword());
        return user;
    }

    @Secondary
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    @Override
    public User get(String id) {
        return super.get(id);
    }
}
