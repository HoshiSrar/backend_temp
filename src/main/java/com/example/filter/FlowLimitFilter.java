package com.example.filter;

import com.example.entity.ResponseBean;
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
    @Value("${limit.ipRequestLimitNumber}")
    Integer ipRequestLimitNumber;
    @Resource
    RedisCache redisCache;
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 对短时间大量请求，超过限制的 ip 进行暂时性封禁。放行所有未被封禁的ip
        String addr = request.getRemoteAddr();
        if (IpHasBan(addr)) {
            WebUtils.renderString(response,ResponseBean.failure(401,"请求过快，请稍后再试").asToJsonString());
        }else {
            tryCount(addr);
            chain.doFilter(request, response);
        }
        //chain.doFilter(request, response);
    }




    /**
     * 检查该 ip 是否有被封禁。
     * @param addIp
     * @return true： 已被封, false：未被封禁，或者该ip第一次请求
     */
    private boolean IpHasBan(String addIp) {
        String blackIp = SystemConstants.VERIFY_BLACK_IP + addIp;
        synchronized (addIp.intern()){
            return  redisCache.hasCacheObject(blackIp);
        }
    }

    /**
     * 将对应 ip 计数器的值 + 1，并将超过限度的 ip 添加进黑名单。并为不存在计数器的 ip 添加计数器
     * @param ip
     * @return true：没有超过 ip 限制访问次数，false: 超过了 ip 限制访问次数。
     */
    private boolean tryCount(String ip){
        String blackIp = SystemConstants.VERIFY_BLACK_IP + ip;
        String countIp = SystemConstants.VERIFY_COUNT_IP + ip;

        // 1. 对应 ip 的计数器是否存在,存在 计数器 + 1, 不存在 添加计数器标识，持续1分钟
        if(Boolean.TRUE.equals(redisCache.hasCacheObject(countIp))){
            redisCache.incrementCacheObjectValue(countIp);
        }else {
            redisCache.setCacheObject(countIp, 1, 60, TimeUnit.SECONDS);
        }
        // 2.  count 是否超过限制。超过，将其封禁 30 s 返回 false， 未超过 返回 true。
        Integer count = redisCache.getCacheObject(countIp);
        if (count>ipRequestLimitNumber) {
            redisCache.setCacheObject(blackIp, "ip ban 30s", 30, TimeUnit.SECONDS);
            return false;
        }else {
            return true;
        }
    }



}
