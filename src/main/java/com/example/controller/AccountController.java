package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.entity.dto.User;
import com.example.entity.dto.UserDetails;
import com.example.entity.dto.UserPrivacy;
import com.example.entity.vo.request.ChangePasswordVo;
import com.example.entity.vo.request.ModifyEmailVo;
import com.example.entity.vo.request.PrivacySaveVo;
import com.example.entity.vo.request.UserDetailsSaveVo;
import com.example.entity.vo.response.AccountVo;
import com.example.entity.vo.response.UserDetailsResVo;
import com.example.entity.vo.response.UserPrivacyVo;
import com.example.service.UserDetailsService;
import com.example.service.UserPrivacyService;
import com.example.service.UserService;
import com.example.utils.BeanCopyUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class AccountController {
    @Resource
    UserService userService;
    @Resource
    UserDetailsService userDetailsService;
    @Resource
    UserPrivacyService privacyService;

    @GetMapping("/info")
    public ResponseBean<AccountVo> info(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        User user = userService.getById(id);
        AccountVo vo = BeanCopyUtils.copyBean(user, AccountVo.class);
        return ResponseBean.success(vo);
    }
    @GetMapping("/details")
    public ResponseBean<UserDetailsResVo> details(@RequestAttribute(SystemConstants.ATTR_USER_ID)int id){
        UserDetails details = Optional
                .ofNullable(userDetailsService.findUserDetailsById(id))
                .orElseGet(UserDetails::new);
        UserDetailsResVo vo = BeanCopyUtils.copyBean(details, UserDetailsResVo.class);
        return ResponseBean.success(vo);
    }
    @PostMapping("/save-details")
    public ResponseBean<Void> saveDetails(@RequestAttribute(SystemConstants.ATTR_USER_ID)int id,
                                          @RequestBody()UserDetailsSaveVo vo){
        boolean success = userDetailsService.saveUserDetails(id, vo);
        return success ? ResponseBean.success() : ResponseBean.failure(400,"此用户名已经被注册");
    }
    @PostMapping("/modify-email")
    public ResponseBean<Void> modifyEmail(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id,
                                          @RequestBody ModifyEmailVo emailVo){
        String result = userService.modifyEmail(id, emailVo);
        return result == null ? ResponseBean.success(): ResponseBean.failure(400, result);
    }
    @PostMapping("/change-password")
    public ResponseBean<Void> changePassword(@RequestAttribute(SystemConstants.ATTR_USER_ID)int id,
                                             @RequestBody ChangePasswordVo passwordVo){
        String result = userService.changePassword(id, passwordVo);
        return result ==null ? ResponseBean.success() : ResponseBean.failure(400, result);
    }
    @PostMapping("/save-privacy")
    public ResponseBean<Void> savePrivacy(@RequestAttribute(SystemConstants.ATTR_USER_ID)int id,
                                             @RequestBody @Valid PrivacySaveVo privacySaveVo){
        privacyService.savePrivacy(id, privacySaveVo);
        return ResponseBean.success();
    }
    @GetMapping("/privacy")
    public ResponseBean<UserPrivacyVo> Privacy(@RequestAttribute(SystemConstants.ATTR_USER_ID)int id){
        UserPrivacy userPrivacy = privacyService.userPrivacy(id);
        UserPrivacyVo privacyVo = BeanCopyUtils.copyBean(userPrivacy, UserPrivacyVo.class);
        return ResponseBean.success(privacyVo);
    }

}
