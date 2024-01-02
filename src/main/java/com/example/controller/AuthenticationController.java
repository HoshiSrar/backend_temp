package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.entity.vo.request.EmailRegisterVo;
import com.example.entity.vo.request.ModifyEmailVo;
import com.example.entity.vo.request.ResetVo;
import com.example.service.UserService;
import com.example.service.impl.EmailServiceImpl;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Resource
    EmailServiceImpl emailService;
    @Resource
    UserService userService;

    @GetMapping("/ask-code")
    public ResponseBean<Void> askVerifyCode(@RequestParam @Email String email,
                                            @RequestParam @Pattern(regexp = "(register|reset|modify)") String type,
                                            HttpServletRequest request){
        String message = emailService.registerEmailVerifyCode(type, email, request.getRemoteAddr());
        return message != null ? ResponseBean.success() : ResponseBean.failure(400,message);
    }

    @PostMapping("/register")
    public ResponseBean<String> registerUser(@RequestBody @Valid EmailRegisterVo registerVo){
        String message = emailService.registerEmailAccount(registerVo);
        return ResponseBean.success(message);
    }
    @PostMapping("/reset-confirm")
    public ResponseBean<String> resetConfirm(@RequestBody ResetVo vo){
        String s = emailService.resetConfirm(vo);
        return ResponseBean.success(s);
    }
    @PostMapping("/reset-password")
    public ResponseBean<String> resetPassword(@RequestBody ResetVo vo){
        String s = emailService.restEmailAccountPassword(vo);
        return ResponseBean.success(s);
    }



}
