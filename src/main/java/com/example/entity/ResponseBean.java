package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record ResponseBean<T>(int code, T data, String message) {
    public static <T> ResponseBean<T> success(T data){
        return new ResponseBean<>(200, data, "请求成功");

    }
    public static <T> ResponseBean<T> success(){
        return success(null);

    }
    public static <T> ResponseBean<T> failure(int code, String message){
        return new ResponseBean<>(code, null, message);

    }


    public static <T> ResponseBean<T> unauthorized(String message){
        return failure(401,message);

    }


    public String asToJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}