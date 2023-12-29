package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.entity.dto.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-12-22 05:10:41
 */
public interface UserService extends IService<User>, UserDetailsService {

    User findUserByNameOrEmail(String text);
    UserDetails loadUserByUsername(String username);

    /**
     * 根据传入 user 的 email 修改密码
     * @param user
     * @return 修改成功返回true，否则false
     */
    boolean updateUserPasswordByEmail(User user);
}
