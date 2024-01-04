package com.example.handler.exceptionHandler;

import com.example.entity.ResponseBean;
import io.minio.errors.MinioException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.GeneralSecurityException;

@RestControllerAdvice
@Slf4j
public class MinioExceptionHandler {

    @ExceptionHandler(MinioException.class)
    public ResponseBean<Void> MinioException(ValidationException e){
        log.error("minio出现异常！[{} : {}]",e.getClass().getName(),e.getMessage());
        return ResponseBean.failure(400,"上传发生错误");
    }

    @ExceptionHandler(GeneralSecurityException.class)
    public ResponseBean<Void> SecurityException(ValidationException e){
        log.error("minio验证出现异常！[{} : {}]",e.getClass().getName(),e.getMessage());
        return ResponseBean.failure(400,"上传发生错误");
    }
}
