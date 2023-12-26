package com.example.utils.constants;

public class SystemConstants
{
    /**
     * redis中属于失效 token 的前缀
     */
    public static final String JWT_BLACK_LIST = "jwt:blacklist: ";
    /**
     * redis中，邮件是否处于限制状态的前缀
     */
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    /**
     * redis中，存储邮箱数据的前缀
     */
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";

    /**
     * 跨域过滤器优先级
     */
    public static final int ORDER_CORS = -102;

}