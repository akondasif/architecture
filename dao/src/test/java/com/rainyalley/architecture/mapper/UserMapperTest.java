package com.rainyalley.architecture.mapper;

import com.rainyalley.architecture.config.DaoConfig;
import com.rainyalley.architecture.entity.UserDo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DaoConfig.class)
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Rollback
    public void insert(){
        UserDo userDo = new UserDo();
        userDo.setName("中文名");
        userDo.setPassword("中文密码");
        userMapper.insert(userDo);
    }
}