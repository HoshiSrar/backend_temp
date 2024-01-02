package com.example.utils.constants;

public class SystemConstants
{
    /**
     * redis中属于失效 token 的前缀
     */
    public static final String JWT_BLACK_LIST = "jwt:blacklist: ";
    /**
     * redis中，邮件是否由于某些情况处于限制状态的前缀
     */
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    /**
     * redis中，存储邮箱数据的前缀
     */
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    /**
     * redis中，存储 ip 封禁黑名单的前缀
     */
    public static final String VERIFY_BLACK_IP = "ip:blacklist";
    /**
     * redis中，存储 ip 计数器的前缀
     */
    public static final String VERIFY_COUNT_IP = "ip:countList";

    /**
     * 跨域过滤器优先级
     */
    public static final int ORDER_CORS = -102;
    /**
     * 限流过滤器优先级
     */
    public static final int ORDER_LIMIT = -101;

    /**
     * 请求自定义属性:id。
     */
    public final static String ATTR_USER_ID = "userId";
    /**
     * 用户角色
     */
    public static final String ROLE_DEFAULT = "admin";
}