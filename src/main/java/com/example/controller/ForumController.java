package com.example.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ResponseBean;
import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.request.TopicUpdateVo;
import com.example.entity.vo.response.*;
import com.example.service.TopicService;
import com.example.service.TopicTypeService;
import com.example.service.WeatherService;
import com.example.utils.BeanCopyUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService weatherService;
    @Resource
    TopicTypeService topicTypeService;
    @Resource
    TopicService topicService;
    @GetMapping("/weather")
    public ResponseBean<WeatherVo> weather(Double longitude,Double latitude){
        System.out.println(longitude+":"+latitude);
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);

        return vo == null ? ResponseBean.failure(400,"获取天气信息异常,请联系管理员"): ResponseBean.success(vo);
    }
    @GetMapping("/types")
    public ResponseBean<List<TopicTypeVo>>  listType(){
        List<TopicType> types = topicTypeService.listType();
        List<TopicTypeVo> vos = BeanCopyUtils.copyBeanList(types, TopicTypeVo.class);
        return ResponseBean.success(vos);
    }
    @PostMapping("/create-topic")
    public ResponseBean<String> creatTopic(@Valid @RequestBody TopicCreateVo topicVo,
                                            @RequestAttribute(SystemConstants.ATTR_USER_ID) int id){

        String result = topicService.createTopic(id, topicVo);
        return result == null ? ResponseBean.success("主题发表成功")
                                : ResponseBean.failure(500, result);
    }

    @GetMapping("/list-topic")
    public ResponseBean<List<TopicPreviewVo>> listTopic(@RequestParam @Min(0) int page,
                                                        @RequestParam @Min(0) int type){
        List<TopicPreviewVo> vo = topicService.listTopicByPage(page+1, type);
        return ResponseBean.success(vo);
    }
    @GetMapping("/top-topic")
    public ResponseBean<List<TopTopicVo>> topTopic(){
        return ResponseBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public ResponseBean<TopicDetailVo> topic(@RequestParam @Min(0) int tid,
                                             @RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        return ResponseBean.success(topicService.getTopic(tid,id));
    }

    @GetMapping("/interact")
    public ResponseBean<Void> interact(@RequestParam @Min(0) int tid,
                                       @RequestParam @Pattern(regexp = "like|collect") String type,
                                       @RequestParam boolean state,
                                       @RequestAttribute(SystemConstants.ATTR_USER_ID)int id){
        topicService.interact(new Interact(tid, id,new Date(), type),state);
        return ResponseBean.success();
    }

    @GetMapping("/collects")
    public ResponseBean<List<TopicPreviewVo>>  collects(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        return ResponseBean.success(topicService.listTopicCollect(id));
    }
    @PostMapping("/update-topic")
    public ResponseBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVo topicVo,
                                          @RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        String result = topicService.updateTopic(topicVo, id);
        return result == null ?ResponseBean.success() : ResponseBean.failure(500,result) ;
    }
    @PostMapping("/add-comment")
    public ResponseBean<Void> addComment(@RequestBody AddCommentVo vo,
                                         @RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        String result = topicService.createComment(id, vo);
        return result == null ? ResponseBean.success():ResponseBean.failure(500, result);
    }
    @GetMapping("/comments")
    public ResponseBean<List<CommentVo>> comments(@RequestParam @Min(0) int tid,
                                                  @RequestParam @Min(0) int page){
        return ResponseBean.success(topicService.comments(tid,page+1));
    }
    @GetMapping("/delete-comment")
    public ResponseBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(SystemConstants.ATTR_USER_ID) int uid){
        topicService.deleteComment(id, uid);
        return ResponseBean.success();
    }

}
