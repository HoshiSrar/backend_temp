package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Topic;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.response.TopTopicVo;
import com.example.entity.vo.response.TopicPreviewVo;

import java.util.List;


/**
 * (Topic)表服务接口
 *
 * @author makejava
 * @since 2024-01-04 21:08:44
 */
public interface TopicService extends IService<Topic> {
    String createTopic(int uid, TopicCreateVo topicVo);
    List<TopicPreviewVo> listTopicByPage(int page,int type);
    List<TopTopicVo> listTopTopics();
}
