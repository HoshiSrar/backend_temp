package com.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import static com.example.utils.constants.SystemConstants.ORDER_CORS;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

/**
 * Cors处理
 */
@Component
@Order(ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Value("${cors.methods}")
    String methods;
    @Value("${cors.origin}")
    String origin;
    @Value("${cors.credentials}")
    boolean credentials;
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        addCorsHeader(request,response);
        if (!Objects.equals(request.getMethod(), OPTIONS.toString())) {
            chain.doFilter(request, response);
        }
        //如果是OPTIONS类型则直接返回回去
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 想请求体中添加允许跨域请求配置
     * @param req
     * @param rep
     */
    private void addCorsHeader(HttpServletRequest req, HttpServletResponse rep){
        rep.addHeader("Access-Control-Allow-Origin", resolveOrigin(req));
        rep.addHeader("Access-Control-Allow-Methods", resolveMethod());
        rep.addHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
        if(credentials) {
            rep.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    /**
     * 解析配置文件中的请求方法
     * @return 解析得到的请求头值
     */
    private String resolveMethod(){
        return methods.equals("*") ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH" : methods;
    }

    /**
     * 解析配置文件中的请求原始站点
     * @param request 请求
     * @return 解析得到的请求头值
     */
    private String resolveOrigin(HttpServletRequest request){
        return origin.equals("*") ? request.getHeader("Origin") : origin;
    }
}
