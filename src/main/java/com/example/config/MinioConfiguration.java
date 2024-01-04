package com.example.config;

import io.minio.MinioClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfiguration {
    @Value("${minio.address}")
    String address;
    @Value("${minio.login.key}")
    String key;
    @Value("${minio.login.password}")
    String password;


    @Bean
    public MinioClient minioClient() {
        log.info("初始化minio，连接{}中",address);
        MinioClient client = MinioClient.builder()
                .endpoint(address)  //对象存储服务地址，注意是9000那个端口
                .credentials(key, password)   //账户直接使用管理员
                .build();
        log.info("初始化minio完成，{}连接成功",address);
        return client;
    }

}
