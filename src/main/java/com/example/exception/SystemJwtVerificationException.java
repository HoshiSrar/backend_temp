package com.example.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class SystemJwtVerificationException extends JWTVerificationException {

    private int code=401;

    private String msg;



    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemJwtVerificationException(String message) {
        super(message);

        this.msg = message;
    }


}
