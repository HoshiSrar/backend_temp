package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.User;
import com.example.entity.vo.request.ChangePasswordVo;
import com.example.entity.vo.request.ModifyEmailVo;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
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
    @Resource
    PasswordEncoder encoder;

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
    public User findUserById(int id) {
        return getById(id);
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
    public String modifyEmail(int id, ModifyEmailVo vo) {
        // 从 redis 中获取该邮箱的验证码
        String emailKey = SystemConstants.VERIFY_EMAIL_DATA + vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(emailKey);
        if (code == null) return "验证码错误或已失效";
        if (!code.equals(vo.getCode())) return "验证码错误";
        stringRedisTemplate.delete(emailKey);
        User user = getById(id);
        if (user != null && user.getId() != id){
            return "邮件已被使用，请更换邮件";
        }
        lambdaUpdate().eq(User::getId, id)
                .set(User::getEmail, vo.getEmail())
                .update();
        return null;
    }

    @Override
    public String changePassword(int id, ChangePasswordVo passwordVo) {
        User user = getById(id);
        if (!encoder.matches(user.getPassword(), passwordVo.getPassword())){
            return "原密码错误，请重新输入";
        }
        boolean success = lambdaUpdate().eq(User::getId, id)
                .set(User::getPassword, encoder.encode(passwordVo.getNew_password()))
                .update();
        return success ? null:"未知错误，请联系管理员";
    }


    @Override
    public User findUserByNameOrEmail(String text) {
        return lambdaQuery()
                .eq(User::getUsername,text).or()
                .eq(User::getEmail,text)
                .one();
    }

    /**
     * 验证 user 是否包含 id 或者 email。
     * @param user
     * @return true 表示有效的 user 对象，false 表示 id 为 0 且 email 不存在
     */
    private boolean verifyUserHasIdOrEmail(User user){
        // 当 id 和 email 同时失效时，返回false
        return !Objects.isNull(user.getId()) || !user.getEmail().isEmpty();

    }

}
