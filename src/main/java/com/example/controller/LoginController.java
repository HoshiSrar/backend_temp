package com.example.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(){
        throw new JWTVerificationException("测试jwt解析错误异常，应该会返回前端数据");
        //return "helloworle";
    }
    @PostMapping("/login2")
    public String login2(){

        return "helloworle";
    }

}
