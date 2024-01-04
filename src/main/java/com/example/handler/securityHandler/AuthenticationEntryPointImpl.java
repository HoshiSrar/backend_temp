package com.example.handler.securityHandler;


import com.alibaba.fastjson2.JSON;
import com.example.entity.ResponseBean;
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
        //认证失败处理器
        e.printStackTrace();
        ResponseBean result = null;
        if(e instanceof BadCredentialsException){
            result = ResponseBean.failure(505,e.getMessage());
        }else if(e instanceof InsufficientAuthenticationException){
            result = ResponseBean.failure(401,"需要登录");
        }else {
            result = ResponseBean.failure(500,"认证获授权失败");
        }
        //响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));
        log.info("认证失败处理结束，已发送响应回前端");

    }
}
