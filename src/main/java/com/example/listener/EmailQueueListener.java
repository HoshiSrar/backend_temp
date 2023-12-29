package com.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 邮件验证码监听器，用时监听发送邮件验证码队列，处理请求。
 */
@Component
@RabbitListener(queues = "mail")
@Slf4j
public class EmailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎注册用户", email,"您的邮件注册验证码为"+code+"有效时间3分钟");
            case "reset" -> createMessage("重置密码", email, "您正在进行重置密码操作，验证码为："+code+"有效时间3分钟，如非本人操作，请无视");
            default -> null;
        };
        if (message==null){
            return;
        }
        // 邮件发送
        try {
            sender.send(message);
        }catch (Exception e){
            log.info("偷懒，发送邮件出现了异常，就打个日志。异常信息：[{}:{},{}]",e.getClass().getName(),e.getMessage(),e.getCause());
        }

    }

    /**
     * 设置通用发送邮件配置，
     * @param title 邮件标题
     * @param email 发送目标邮件对象
     * @param context 邮件内容
     * @return 通用邮件
     */
    private SimpleMailMessage createMessage(String title ,String email,String context){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setTo(email);
        message.setText(context);
        message.setFrom(username);
        return message;
    }
}
