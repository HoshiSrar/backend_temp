package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.example.entity.dto.TopicComment;
import com.example.mapper.TopicCommentMapper;
import com.example.service.TopicCommentService;

/**
 * (TopicComment)表服务实现类
 *
 * @author makejava
 * @since 2024-01-08 19:55:19
 */
@Service("topicCommentService")
public class TopicCommentServiceImpl extends ServiceImpl<TopicCommentMapper, TopicComment> implements TopicCommentService {

}
