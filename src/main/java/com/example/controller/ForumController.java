package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.entity.vo.response.WeatherVo;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseBean<WeatherVo> weather(Double longitude,Double latitude){
        System.out.println(longitude+":"+latitude);
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);

        return vo == null ? ResponseBean.failure(400,"获取天气信息异常,请联系管理员"): ResponseBean.success(vo);
    }

}
