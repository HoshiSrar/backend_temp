package com.example.handler.exceptionHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.entity.ResponseBean;
import com.example.exception.SystemJwtVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class JwtExceptionHandler {
    /**
     * JWT解析错误，返回前端错误信息
     * @param e
     * @return
     */
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseBean JwtVerificationException(JWTVerificationException e){
        SystemJwtVerificationException exception = new SystemJwtVerificationException("Jwt解析错误");
        log.error("Jwt异常！",e);
        return ResponseBean.failure(302,exception.getMsg());
    }
}
