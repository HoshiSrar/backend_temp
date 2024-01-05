package com.example.utils;

import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *  限流工具类
 */
@Component
public class FlowUtils {
    private static final LimitAction defaultAction = overclock -> !overclock;
    @Resource
    StringRedisTemplate template;

    /**
     *  根据邮箱冷却关键词 key 查询邮箱是否冷却完毕，更改为冷却状态，冷却时间 blockTime。
     * @param key 邮箱冷却（block）关键字
     * @param blockTime 邮箱请求的限制时间，单位秒。
     * @return true：邮箱可用。false：不可使用
     */
    public boolean limitEmailOnceCheck(String key,int blockTime){
        if (Boolean.TRUE.equals(template.hasKey(key))){
            return false;
        }else {
            template.opsForValue().set(key, "这是邮箱限制关键字，共持续"+blockTime+"秒",blockTime, TimeUnit.SECONDS);
            return true;
        }
    }

    /**
     * 在某个时间段内请求次数是否受限，如3秒内20次请求。
     * @param countKey 计数关键词 key
     * @param frequency 请求频率 次数
     * @param period 计数周期(/s)
     * @return 是否通过检查。
     */
    public boolean limitPeriodCounterCheck(String countKey,int frequency,int period){
        return internalCheck(countKey, frequency, period, defaultAction);
    }

    /**
     * 内部使用，请求流量限制主要逻辑
     * @param countKey 计数键种类
     * @param frequency 请求频率
     * @param period 计数周期
     * @param action 限制行为与策略
     * @return 是否通过限流检查
     */
    private boolean internalCheck(String countKey,int frequency,int period,LimitAction action){
        if (Boolean.TRUE.equals(template.hasKey(countKey))){
            Long value = Optional.ofNullable(template.opsForValue().increment(countKey)).orElse(0L);
            return action.run(value > frequency);
        }else {
            template.opsForValue().set(countKey, "1",period,TimeUnit.SECONDS);
            return true;
        }
    }

    //内部使用，限制行为与策略
    private interface LimitAction{
        boolean run(boolean overclock);
    }


    @Value("${limit.ipRequestLimitNumber}")
    Integer ipRequestLimitNumber;
    @Resource
    RedisCache redisCache;
    /**
     * 检查该 ip 是否在封禁名单中。
     * @param addIp
     * @return true： 已被封, false：未被封禁，或者该ip第一次请求
     */
    public boolean IpHasBan(String addIp) {
        String blackIp = SystemConstants.VERIFY_BLACK_IP + addIp;
        synchronized (addIp.intern()){
            return  redisCache.hasCacheObject(blackIp);
        }
    }

    /**
     * 尝试使访问 ip 计数器值++，并将高于访问次数的 ip 添加进封禁名单中。第一次访问会添加对应的计数器
     * @param ip 尝试计数的 ip 地址
     * @return true：没有超过 ip 访问次数限制。false: 超过限制，封禁 ip 30s。
     */
    public boolean tryCount(String ip){
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
