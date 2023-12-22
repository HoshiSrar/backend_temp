package com.example.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //@Override
    //public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    //    // 创建 Fastjson 消息转换器
    //
    //    FastJsonHttpMessageConverter converter =  new JsonbHttpMessageConverter();
    //
    //    // 添加 Fastjson 配置，比如是否格式化返回的 JSON 数据
    //    // converter.setFastJsonConfig(config);
    //
    //    // 将 Fastjson 消息转换器添加到 Spring Boot 的消息转换器列表
    //    converters.add(converter);
    //}
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
