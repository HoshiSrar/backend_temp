package com.example.service.impl;

import com.example.entity.dto.User;
import com.example.entity.vo.request.EmailRegisterVo;
import com.example.service.EmailService;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
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
    @Resource
    PasswordEncoder Encoder;
    @Value("${limit.time}")
    int limitTime;

   @Resource
   UserServiceImpl userService;


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
            //  将邮件验证码放入 redis 中
            StringRedisTemplate.opsForValue()
                    .set(SystemConstants.VERIFY_EMAIL_DATA+emailAddress, String.valueOf(code),3, TimeUnit.MINUTES);
            return "发送验证码请求成功";
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVo vo) {
        String email = vo.getMail();
        String username = vo.getUsername();
        // 从 redis 中获取该邮箱的验证码
        String key = SystemConstants.VERIFY_EMAIL_DATA + email;
        String code = StringRedisTemplate.opsForValue().get(key);
        //校验文本信息
        if(code == null) return "请重新申请验证码";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        if (existsUserByEmail(email)){
            return "此电子邮箱已被注册";
        }
        if(existsUserByUsername(username)){
            return "此用户名已被注册";
        }
        // 开始注册操作
        String passwordEncode = Encoder.encode(vo.getPassword());
        User user = new User().setUsername(username)
                .setPassword(passwordEncode)
                .setEmail(email)
                .setRole("common");
        if(userService.save(user)){
            StringRedisTemplate.delete(key);
            return "注册成功:";
        }else {
            return "内部出现错误，请联系管理员";
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

    /**
     * 查询数据库中是否存在与 email 一样的值。
     * @param email
     * @return 存在为 true，不存在为false。
     */
    private boolean existsUserByEmail(String email){
        User user = userService.findUserByNameOrEmail(email);
        return Optional.ofNullable(user).isPresent();
    }
    /**
     * 查询数据库中是否存在与 username 一样的值。
     * @param username
     * @return 存在为 true，不存在为false。
     */
    private boolean existsUserByUsername(String username){
        User user = userService.findUserByNameOrEmail(username);
        return Optional.ofNullable(user).isPresent();
    }
}
