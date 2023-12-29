package com.example.service;

import com.example.entity.vo.request.EmailRegisterVo;
import com.example.entity.vo.request.ResetVo;

public interface EmailService {
    /**
     * 根据传入的type类型，向邮箱发送验证码邮件
     * @param Type 请求发送的邮件类型，如登录验证码，修改密码验证码
     * @param emailAddress 发送的邮箱地址
     * @param ip 请求发送邮件的ip地址
     * @return 验证码，使用消息队列异步返回
     */
    String registerEmailVerifyCode(String Type,String emailAddress,String ip);

    /**
     * 注册用户
     * @param vo 实体
     * @return 成功信息或者失败信息
     */
    String registerEmailAccount(EmailRegisterVo vo);

    /**
     * 验证 RestVo 中的修改密码 code 和 email 是否对应。
     * @param vo
     * @return
     */
    String resetConfirm(ResetVo vo);

    /**
     * 根据传入的 vo 中的 email ，code ，password 验证并修改用户密码。
     * @param vo
     * @return
     */
    String restEmailAccountPassword(ResetVo vo);

}
