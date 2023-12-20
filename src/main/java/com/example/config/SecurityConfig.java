package com.example.config;

import com.example.Entity.ResponseResult;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authConf)-> authConf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(fromConf-> fromConf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(ResponseResult.failure(401,exception.getMessage()).asToJsonString());
                        })
                        .successHandler((request, response, authentication) -> {
                            //todo 默认返回一个登录成功，实际需要查询数据库并返回相应数据
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(ResponseResult.ok("登录成功").asToJsonString());
                        })
                )
                .logout(logoutConf-> logoutConf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(ResponseResult.ok("退出成功").asToJsonString());
                        })
                )
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionConf->{sessionConf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
