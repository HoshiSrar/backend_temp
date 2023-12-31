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
    public static final String VERIFY_BLACK_IP = "ip:blacklist:";
    /**
     * redis中，存储 ip 计数器的前缀
     */
    public static final String VERIFY_COUNT_IP = "ip:countList:";
    /**
     * 过滤器优先级
     */
    public static final int ORDER_CORS = -102;
    public static final int ORDER_LIMIT = -101;
    /**
     * 请求自定义属性:id。
     */
    public final static String ATTR_USER_ID = "userId";
    /**
     * 用户角色
     */
    public static final String ROLE_DEFAULT = "admin";
    // 天气缓存相关
    public static final String FORUM_WEATHER_CACHE = "weather:cache:";
    // 论坛相关
    public static final String FORUM_IMAGE_COUNTER = "forum:image:";
    // 发帖限制
    public static final String FORUM_TOPIC_COUNTER = "forum:topic:creat:count:";
    // 发帖限制
    public static final String FORUM_TOPIC_COMMENT = "forum:topic:comment:count:";
    // 主题浏览缓存
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "topic:preview:";
}