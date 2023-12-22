package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String jwtKey;
    @Value("${spring.security.jwt.expire}")
    private int expires;

    @Resource
    RedisCache redisCache;

    /**
     * 创建jwt令牌,设置内部保存数据,使用 HMAC256 进行加密，
     * @param details 用户详细信息
     * @param id 用户id
     * @param username 用户名
     * @return jwt String：包含了用户信息，id，过期时间等等信息。
     */
    public String createJwt(UserDetails details,int id ,String username){
        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        Date expire = expireTime();
        return JWT.create()
                .withJWTId(String.valueOf(UUID.randomUUID()))
                .withClaim("id",id)
                .withClaim("name", username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 将 token String 解析成DecodeJWY对象
     * @param jwt token字符串
     * @return DecodedJWT 对象
     */
    public  DecodedJWT parseToJWT(String jwt){
        String jwtToken = convertToken(jwt);
        if (jwtToken == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        // todo 这里可能会有bug，全局处理了异常 ，可能不会返回null给调用方法
        DecodedJWT verify = jwtVerifier.verify(jwtToken);
        //
        if (isInvalidToken(verify.getId())) return null;
        Date expiresAt = verify.getExpiresAt();
        // 未过期的令牌可以返回
        return new Date().after(expiresAt) ? null : verify;

    }

    /**
     * 废弃 JWT ，通过将 jwtId 记入 Redis 黑名单中实现。当黑名单中存在 jwtId 时表示该jwt已经失效。
     * @param headerToken
     * @return true : 成功删除，<p/>false：该令牌已处于逻辑删除状态，或jwt本身错误（如过期）。
     */
    public boolean invalidateJwt(String headerToken){
        String jwtToken = convertToken(headerToken);
        if (jwtToken==null) return false;

        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = jwtVerifier.verify(jwtToken);
            return deleteToken(jwt.getId(),jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }

    }

    /**
     * 逻辑删除jwt令牌，将 jwt 拉入失效黑名单中，redis保存时间为 jwt 剩余有效时间
     * @param uuid jwt的id，
     * @param time jwt过期时间
     * @return true：逻辑删除jwt成功，false：黑名单含有该jwt，已处于逻辑删除中
     */
    private boolean deleteToken(String uuid,Date time){
        if (isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        redisCache.setCacheObject(SystemConstants.JWT_BLACK_LIST+uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * 根据 uuid 查询 Redis 黑名单是否该 jwt 存在，判断 jwt 是否失效
     * @param uuid String，jwt的id
     * @return true：失效jwt，false：有效jwt
     */
    private boolean isInvalidToken(String uuid){
        return redisCache.hasCacheObject(SystemConstants.JWT_BLACK_LIST+uuid);
    }
    /**
     * jwt转换为UserDetails
     * @param jwt
     * @return UserDetail
     */
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();


        return User.withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    /**
     * 获取jwt中的id
     * @param jwt
     * @return int
     */
    public Integer toId(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }


    /**
     * 获取设定的过期时间
     * @return 令牌失效的日期
     */
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expires*24);
        return calendar.getTime();
    }

    /**
     * 分割完整请求中的jwt: <p/>Bearer xxxxxxx-> xxxxxxx
     * @param  jwt
     * @return  jwt
     */
    private String convertToken(String jwt){
        if (jwt ==null || !jwt.startsWith("Bearer ")){
            return null;
        }
        return jwt.substring(7);
    }
}
