package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.UserDetails;
import com.example.entity.vo.request.UserDetailsSaveVo;


/**
 * (UserDetails)表服务接口
 *
 * @author makejava
 * @since 2023-12-31 20:59:50
 */
public interface UserDetailsService extends IService<UserDetails> {
    UserDetails findUserDetailsById(int id);
    boolean saveUserDetails(int id, UserDetailsSaveVo detailsSaveVo);
}
