package com.example.config;

import com.example.entity.ResponseBean;
import com.example.entity.vo.response.UserVo;
import com.example.filter.JwtAuthenticationTokenFilter;
import com.example.service.impl.UserServiceImpl;
import com.example.utils.JwtUtils;
import com.example.utils.WebUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@Slf4j
public class SecurityConfig {

    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtAuthenticationTokenFilter jwtFilter;

    @Resource
    AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    AccessDeniedHandler accessDeniedHandler;

    @Resource
    UserServiceImpl service;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authConf)-> authConf
                        .requestMatchers("/api/auth/**","/error","/test/*").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(fromConf-> fromConf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)//登录失败处理器
                        .successHandler(this::onAuthenticationSuccess)//登录成功处理器
                )
                .logout(logoutConf-> logoutConf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)//退出成功处理器
                )
                //session管理，关闭默认的session认证
                .sessionManagement(sessionConf-> sessionConf

                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e->{e
                        //认证失败
                        .authenticationEntryPoint(authenticationEntryPoint)
                        //授权失败
                        .accessDeniedHandler(accessDeniedHandler);
                })
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }




    //登录成功处理器
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer id = service.findUserByNameOrEmail(user.getUsername()).getId();
        //创建jwt
        String jwtToken = jwtUtils.createJwt(user, id, user.getUsername());
        UserVo userVo = new UserVo()
                .setExpire(jwtUtils.expireTime())
                .setRole(user.getAuthorities().toString())
                .setToken(jwtToken)
                .setUserName(user.getUsername());
        WebUtils.renderString(response, ResponseBean.success(userVo).asToJsonString());
    }

    //登录失败处理器
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        log.info("登录失败");
        WebUtils.renderString(response, ResponseBean.failure(401,exception.getMessage()).asToJsonString());
    }

    //退出成功处理器
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");
        // 判断jwt是否可以废弃，成功废弃返回退出成功信息，否则返回退出失败
        if (jwtUtils.invalidateJwt(authorization)){
            WebUtils.renderString(response, ResponseBean.success("退出成功").asToJsonString());
        }else {
            WebUtils.renderString(response, ResponseBean.success("jwt错误，退出失败").asToJsonString());
        }
    }
}
