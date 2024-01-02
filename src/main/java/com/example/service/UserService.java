package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.entity.dto.User;
import com.example.entity.vo.request.ChangePasswordVo;
import com.example.entity.vo.request.ModifyEmailVo;
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

    User findUserById(int id);
    /**
     * 根据传入 user 的 email 修改密码
     * @param user
     * @return 修改成功返回true，否则false
     */
    boolean updateUserPasswordByEmail(User user);

    /**
     * 根据 id 和 vo 中的验证码是否一致选择是否更新邮箱信息。
     * @param id
     * @param vo
     * @return
     */
    String modifyEmail(int id, ModifyEmailVo vo);

    String changePassword(int id, ChangePasswordVo passwordVo);
}
