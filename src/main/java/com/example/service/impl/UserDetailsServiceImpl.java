package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.User;
import com.example.entity.vo.request.UserDetailsSaveVo;
import com.example.service.UserService;
import com.example.utils.BeanCopyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.entity.dto.UserDetails;
import com.example.mapper.UserDetailsMapper;
import com.example.service.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * (UserDetails)表服务实现类
 *
 * @author makejava
 * @since 2023-12-31 20:59:50
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl extends ServiceImpl<UserDetailsMapper, UserDetails> implements UserDetailsService {
    @Resource
    UserService userService;

    @Override
    public UserDetails findUserDetailsById(int id) {
        return getById(id);
    }

    @Override
    @Transactional
    public synchronized boolean saveUserDetails(int id, UserDetailsSaveVo detailsVo) {
        // 判断用户名是否被注册过
        User user = userService.findUserByNameOrEmail(detailsVo.getUsername());
        if (Optional.ofNullable(user).isEmpty() || user.getId() == id){
            userService.lambdaUpdate()
                    .eq(User::getId,id)
                    .set(User::getUsername, detailsVo.getUsername())
                    .update();
            UserDetails copyBean = BeanCopyUtils.copyBean(detailsVo, UserDetails.class);
            copyBean.setId(id);
            saveOrUpdate(copyBean);
            return true;
        }
        return false;
    }
}
