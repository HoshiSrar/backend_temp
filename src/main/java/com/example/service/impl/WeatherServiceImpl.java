package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVo;
import com.example.service.WeatherService;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${weather.api}")
    String weatherAPI;
    @Value("${weather.key}")
    String key;
    @Resource
    RestTemplate restTemplate;

    @Resource
    StringRedisTemplate StringredisTemplate;

    @Override
    public WeatherVo fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude, latitude);
    }

    /**
     * 根据 经纬度 获取天气 vo 对象，从 api 或者 redis 缓存中获取。
     * @param longitude
     * @param latitude
     * @return 天气对象
     */
    private WeatherVo fetchFromCache(double longitude, double latitude){
        String url = weatherAPI + longitude + "," + latitude + "&key=" + key;
        // 从 api 查询城市地理位置等信息，gzip 对象，需要转换
        byte[] data = restTemplate.getForObject(url,byte[].class);
        // gzip => JSON 对象
        JSONObject jsonObject = deCompressStringToJSON(data);
        if (jsonObject == null){
            return null;
        }
        JSONObject location = jsonObject.getJSONArray("location").getJSONObject(0);

        Integer id = location.getInteger("id");
        String key = SystemConstants.FORUM_WEATHER_CACHE +id;
        // 根据城市 id 从 cache 中查询天气。
        String cache = StringredisTemplate.opsForValue().get(key);
        if (cache != null){
            return JSONObject.parseObject(cache).to(WeatherVo.class);
        }
        // 缓存没有，转为从 Api 中查询
        WeatherVo vo = fetchFromAPI(id, location);
        if (vo == null) {
            log.info("天气 api 查询结果为 null");
            return null;
        }
        // 保存 rides 中 1 小时
        StringredisTemplate.opsForValue().set(key, JSONObject.from(vo).toJSONString(),1, TimeUnit.HOURS);
        return vo;
    }

    /**
     * 根据城市 id 向第三方 api 查询天气信息
     * @param id 城市id
     * @param location 包含了城市地址信息的 JSOn 对象
     * @return 天气对象
     */
    private WeatherVo fetchFromAPI(int id,JSONObject location){
        WeatherVo vo = new WeatherVo();
        vo.setLocation(location);
        // 从第三方 api 接口查询 天气信息, 并将 gzip 格式转为 json 对象。
        JSONObject now = deCompressStringToJSON(restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/now?location="+id+"&key="+key,byte[].class));
        if(now == null) return null;
        // 获取当前天气数据
        vo.setNow(now.getJSONObject("now"));
        //获取最近 24h 的数据
        JSONObject hourly = deCompressStringToJSON(restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/24h?location="+id+"&key="+key,byte[].class));
        if(hourly == null) return null;

        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;

    }
    /**
     * 将 二进制 gzip 格式数据转换为 json 对象。
     * @param data byte[]
     * @return  JSON object
     */
    private JSONObject deCompressStringToJSON(byte[] data){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzip.read(buffer)) != -1){
                stream.write(buffer,0,read);
            }
            gzip.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        }catch (IOException e) {
            log.info("giz处理出错，[{}:{}]",e.getCause(),e.getCause());
            return null;
        }
    }
}
