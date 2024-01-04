package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class WeatherVo {
    private JSONObject location;
    private JSONObject now;
    private JSONArray hourly;
}
