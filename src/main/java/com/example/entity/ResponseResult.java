package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record ResponseResult<T>(int code, T data, String message) {
    public static <T> ResponseResult<T> success(T data){
        return new ResponseResult<>(200, data, "请求成功");

    }
    public static <T> ResponseResult<T> success(){
        return success(null);

    }
    public static <T> ResponseResult<T> failure(int code,String message){
        return new ResponseResult<>(code, null, message);

    }


    public static <T> ResponseResult<T> unauthorized(String message){
        return failure(401,message);

    }


    public String asToJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}