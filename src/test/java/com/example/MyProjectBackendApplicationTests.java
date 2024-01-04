package com.example;

import com.alibaba.fastjson2.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.entity.dto.User;
import com.example.service.impl.UserServiceImpl;
import com.example.utils.RedisCache;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.Encoder;
import java.util.Base64;
import java.util.Date;

@SpringBootTest
class MyProjectBackendApplicationTests {
    @Resource
    UserServiceImpl userService;
    @Resource
    RedisCache redisCache;
    @Resource
    PasswordEncoder encoder;
    @Test
    void contextLoads() {

        User user = new User()
                //.setId(2)
                //.setUsername("test")
                .setPassword("5555")
                .setEmail("qq.com")
                .setRole("admin");
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, "test");
        userService.update(user, wrapper);
    }
    @Test
    void redisTest(){
        redisCache.setCacheObject("test", "test的值");
        redisCache.setCacheObject("bloglogin:"+1, "123");
        redisCache.setCacheObject("你好", "全是中文");
        System.out.println(redisCache.keys("test"));
    }
    @Test
    void encodePassword(){
    }


}
