package com.example.Entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record ResponseResult<T>(int code, T data, String message) {
    public static <T> ResponseResult<T> ok(T data){
        return new ResponseResult<>(200, data, "请求成功");

    }
    public static <T> ResponseResult<T> ok(){
        return ok(null);

    }
    public static <T> ResponseResult<T> failure(int code,String message){
        return new ResponseResult<>(code, null, message);

    }

    public String asToJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}