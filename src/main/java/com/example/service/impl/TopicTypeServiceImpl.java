package com.example.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.entity.dto.TopicType;
import com.example.mapper.TopicTypeMapper;
import com.example.service.TopicTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (Topic)表服务实现类
 *
 * @author makejava
 * @since 2024-01-04 21:02:09
 */
@Service("topicTypeService")
public class TopicTypeServiceImpl extends ServiceImpl<TopicTypeMapper, TopicType> implements TopicTypeService {

    @Override
    public List<TopicType> listType() {
        return list();
    }
}
