package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *  限流工具类
 */
@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate template;

    /**
     *  根据 key 向 Redis 查询请求邮箱是否处于冷却状态。<br/>请求多次，请求限制时间未结束，封禁等原因都会导致处于冷却。
     * @param key 标识关键字
     * @param blockTime 发送邮箱请求的限制时间，单位秒。
     * @return true表示通过，不处于限制状态。false表示不通过
     */
    public boolean limitOnceCheck(String key,int blockTime){
        // 查询 redis 中是否包含该请求 key，如果存在则不允许通过，返回false
        if (Boolean.TRUE.equals(template.hasKey(key))){
            return false;
        }else {
            template.opsForValue().set(key, "这是邮箱限制关键字，共持续"+blockTime+"秒",blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
