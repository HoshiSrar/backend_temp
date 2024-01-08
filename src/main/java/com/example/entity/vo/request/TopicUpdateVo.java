package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TopicUpdateVo {
    @Min(0)
    int id;
    @Min(0)
    @Max(5)
    int type;
    String title;
    JSONObject content;

}
