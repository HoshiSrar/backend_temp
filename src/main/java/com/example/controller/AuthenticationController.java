package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.service.impl.EmailServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Resource
    EmailServiceImpl emailService;

    @GetMapping("/ask-code")
    public ResponseBean<Void> askVerifyCode(@RequestParam @Email String email,
                                            @RequestParam @Pattern(regexp = "(register|reset)") String type,
                                            HttpServletRequest request){
        String message = emailService.registerEmailVerifyCode(type, email, request.getRemoteAddr());
        return message == null ? ResponseBean.success() : ResponseBean.failure(400,message);

    }

}
