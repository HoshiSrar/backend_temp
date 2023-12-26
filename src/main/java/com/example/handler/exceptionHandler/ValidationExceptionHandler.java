package com.example.handler.exceptionHandler;

import com.example.entity.ResponseBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseBean<Void> JwtVerificationException(ValidationException e){
        log.error("请求参数异常！[{} : {}]",e.getClass().getName(),e.getMessage());
        return ResponseBean.failure(400,"请求参数有误");
    }
}
