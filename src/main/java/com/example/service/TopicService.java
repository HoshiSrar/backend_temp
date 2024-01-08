package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.request.TopicUpdateVo;
import com.example.entity.vo.response.CommentVo;
import com.example.entity.vo.response.TopTopicVo;
import com.example.entity.vo.response.TopicDetailVo;
import com.example.entity.vo.response.TopicPreviewVo;
import com.example.utils.constants.SystemConstants;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

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
    TopicDetailVo getTopic(int tid, int uid);
    void interact(Interact interact, boolean state);
    List<TopicPreviewVo> listTopicCollect(int uid);
    String updateTopic(TopicUpdateVo topicVo,int uid);
    String createComment(int uid, AddCommentVo vo);
    List<CommentVo> comments(int tid,int pageNumber);
    void deleteComment(int id,int uid);
}
