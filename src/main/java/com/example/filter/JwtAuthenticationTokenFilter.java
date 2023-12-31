package com.example.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JwtUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * jwt过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    JwtUtils jwtUtils;

    /**
     * 解析请求头中的jwt过滤器
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中的token
        String token = request.getHeader("Authorization");
        DecodedJWT jwt = null;
        try {
            jwt = jwtUtils.parseToJWT(token);
        }catch (JWTVerificationException e){
            log.info("发生了JWT错误，全局异常处理不到，打个标记");
        }

        if (jwt!=null){
            UserDetails user = jwtUtils.toUser(jwt);
            // 生成 security可使用的 token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user,null, user.getAuthorities());
            //token添加Request的相关信息作为 UserDetails 的详细信息
            authenticationToken.setDetails(request.getAuthType());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            request.setAttribute(SystemConstants.ATTR_USER_ID, jwtUtils.toId(jwt));

        }
        filterChain.doFilter(request, response);

    }
}
