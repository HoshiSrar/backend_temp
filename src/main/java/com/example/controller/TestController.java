package com.example.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/login")
    public String login(){
        throw new JWTDecodeException("测试jwt错误，框架内部异常测试");
                //JWTVerificationException("测试jwt解析错误异常，应该会返回前端数据");
        //return "helloworle";
    }
    @PostMapping("/login2")
    public String login2(){

        return "helloworle";
    }

}
