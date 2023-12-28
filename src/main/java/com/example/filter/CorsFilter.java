package com.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.utils.constants.SystemConstants.ORDER_CORS;

/**
 * Cors处理
 */
@Component
@Order(ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Value("${cors.allowable}")
    String corsAllowableAddress;
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        addCorsHeader(request,response);

        chain.doFilter(request, response);
    }

    /**
     * 想请求体中添加允许跨域请求配置
     * @param req
     * @param rep
     */
    private void addCorsHeader(HttpServletRequest req, HttpServletResponse rep){
        rep.addHeader("Access-Control-Allow-Origin", corsAllowableAddress);
        rep.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,HEAD,OPTIONS,DELETE");
        rep.addHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
    }
}
