package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScans({
        @MapperScan("com/example/mapper")
})
public class MyProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjectBackendApplication.class, args);
    }

}
