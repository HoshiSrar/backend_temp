package com.example.service.impl;

import com.example.service.EmailService;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate StringRedisTemplate;
    @Resource
    FlowUtils flowUtils;
    @Value("${limit.time}")
    int limitTime;

    @Override
    public String registerEmailVerifyCode(String type, String emailAddress, String ip) {
        // 枷锁，防止大量请求导致查询 redis 判断异常
        synchronized (ip.intern()){
            if (!verifyEmailLimit(ip)){
                return "请求验证码频繁，请稍后再试。";
            }
            int code = getRandomCode();
            Map<String, Object> data = Map.of("type",type,"email",emailAddress,"code",code);
            //  将需要消费的邮件信息放进队列了
            amqpTemplate.convertAndSend("mail",data);

            StringRedisTemplate.opsForValue()
                    .set(SystemConstants.VERIFY_EMAIL_DATA+emailAddress, String.valueOf(code),3, TimeUnit.MINUTES);
            return "发送验证码请求成功";
        }
    }



    /**
     * 获取6位随机数字
     * @return 获得6位随机数字
     */
    private int getRandomCode(){
        Random random = new Random();
        // 生成随机 6 位数字
        int min = 100000;
        int max = 900000;
        return random.nextInt(max) + min;
    }
    private boolean verifyEmailLimit(String ip){
        String key = SystemConstants.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, limitTime);
    }
}
