package com.example.config.securityHandler;


import com.alibaba.fastjson2.JSON;
import com.example.entity.ResponseResult;
import com.example.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;
@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {
        /**认证失败处理器*/
        e.printStackTrace();
        log.info("认证处理处理器开始");
        ResponseResult result = null;
        if(e instanceof BadCredentialsException){
            result = ResponseResult.failure(505,e.getMessage());
        }else if(e instanceof InsufficientAuthenticationException){
            result = ResponseResult.failure(401,"需要登录");
        }else {
            result = ResponseResult.failure(500,"认证获授权失败");
        }
        log.info("认证处理器结束");
        //响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));

    }
}
