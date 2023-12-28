package com.example.handler.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常已知可以处理 controller 层的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(RuntimeException.class)
    public void RunExceptionHandler(RuntimeException e){
        log.info("出现异常,[{}]",e.getMessage());
    }
}
