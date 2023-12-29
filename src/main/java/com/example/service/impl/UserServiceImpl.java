package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-12-22 05:10:42
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByNameOrEmail(username);
        if (user==null) throw new UsernameNotFoundException("用户名或密码错误");
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    @Override
    public boolean updateUserPasswordByEmail(User user) {
        boolean update = lambdaUpdate().eq(User::getEmail, user.getEmail())
                .set(User::getPassword, user.getPassword())
                .update();
        if (update){
            stringRedisTemplate.delete(SystemConstants.VERIFY_EMAIL_DATA + user.getEmail());
            return true;
        }
        return false;

    }

    @Override
    public User findUserByNameOrEmail(String text) {
        return lambdaQuery()
                .eq(User::getUsername,text).or()
                .eq(User::getEmail,text)
                .one();
    }

    /**
     * 验证 user 是否包含的 id 或者 email。
     * @param user
     * @return true 表示有效的 user 对象，false 表示 id 为 0 且 email 不存在
     */
    private boolean verifyUserHasIdOrEmail(User user){
        // 当 id 和 email 同时失效时，返回false
        return !Objects.isNull(user.getId()) || !user.getEmail().isEmpty();

    }

}
