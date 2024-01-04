package com.example.filter;

import com.example.entity.ResponseBean;
import com.example.utils.FlowUtils;
import com.example.utils.RedisCache;
import com.example.utils.WebUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 请求流量限制，redis实现
 */
@Component
@Order(SystemConstants.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {
    @Resource
    FlowUtils flowUtils;
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 对短时间大量请求，超过限制的 ip 进行暂时性封禁。放行所有未被封禁的ip
        String addr = request.getRemoteAddr();
        if (flowUtils.IpHasBan(addr)) {
            WebUtils.renderString(response,ResponseBean.failure(401,"请求过快，请稍后再试").asToJsonString());
        }else {
            flowUtils.tryCount(addr);
            chain.doFilter(request, response);
        }
        //chain.doFilter(request, response);
    }
}
