package com.example.config.securityHandler;


import com.alibaba.fastjson2.JSON;
import com.example.entity.ResponseResult;
import com.example.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;



import java.io.IOException;
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
        /**授权失败处理器*/
        log.info("权限不足");
        e.printStackTrace();

        ResponseResult result = ResponseResult.unauthorized("无权限操作");
        //响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
