package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.ResponseBean;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.response.TopTopicVo;
import com.example.entity.vo.response.TopicPreviewVo;
import com.example.mapper.TopicMapper;
import com.example.service.TopicService;
import com.example.service.TopicTypeService;
import com.example.utils.BeanCopyUtils;
import com.example.utils.CacheUtils;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * (Topic)表服务实现类
 *
 * @author makejava
 * @since 2024-01-04 21:08:44
 */
@Service("topicService")
@Slf4j
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeService topicTypeService;
    @Resource
    FlowUtils flowUtils;
    @Resource
    CacheUtils cacheUtils;


    @Override
    public String createTopic(int uid, TopicCreateVo topicVo) {
        if (!textLimitCheck(topicVo.getContent())) {
            return "文章内容过多，发布失败";
        }
        Set<Integer> types = topicTypeService.listType().stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());

        if (!types.contains(Integer.parseInt(topicVo.getType()))){
            return "文章类型非法";
        }
        // 每小时只能发3篇
        boolean check = flowUtils.limitPeriodCounterCheck(SystemConstants.FORUM_TOPIC_COUNTER, 3, 3600);
        if (!check){
            return "发文达到上限，请稍后再试";
        }
        Topic topic = BeanCopyUtils.copyBean(topicVo, Topic.class);
        topic.setContent(topicVo.getContent().toJSONString());
        topic.setUid(uid);
        try {
            if (save(topic)){
                cacheUtils.deleteCachePattern(SystemConstants.FORUM_TOPIC_PREVIEW_CACHE + "*");
                return null;
            }
        }catch (Exception e){
            log.info("保存帖子数据库出错，[信息：{}，原因：{}]",e.getMessage(),e.getCause());
            return "出现错误，请联系管理员";
        }return "内部错误，请联系管理员";
    }

    @Override
    public List<TopicPreviewVo> listTopicByPage(int page, int type) {
        String key = SystemConstants.FORUM_TOPIC_PREVIEW_CACHE + page +":"+type;
        List<TopicPreviewVo> list = cacheUtils.takeListFromCache(key, TopicPreviewVo.class);
        if (list != null) return list;
        List<Topic> topics;
        if (type == 0) {
            topics = baseMapper.topicList(page * 10);
        }else {
            topics = baseMapper.topicListByType(page * 10,type);
        }
        if (topics.isEmpty()) return null;
        list = topics.stream().map(topic -> resolveToPreview(topic)).toList();
        cacheUtils.saveListToCache(key,list,60);
        return list;
    }

    @Override
    public List<TopTopicVo> listTopTopics() {
        List<Topic> topics = query().
                select("id","title","top")
                .eq("top", '1')
                .list();
        return topics.stream().map(topic -> BeanCopyUtils.copyBean(topic, TopTopicVo.class)).toList();
    }
    private TopicPreviewVo resolveToPreview(Topic topic){
        TopicPreviewVo vo = new TopicPreviewVo();
        vo = BeanCopyUtils.copyBean(topic,TopicPreviewVo.class);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text){
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setText(previewText.length() > 300 ? previewText.substring(0,300):previewText.toString());
        vo.setImages(images);
        vo.setTime(new Date());
        return vo;
    }


    private boolean textLimitCheck(JSONObject jSONText){
        if (jSONText == null) return false;
        long length = 0;
        for (Object op : jSONText.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > 20000) return false;
        }
        return true;
    }
}
