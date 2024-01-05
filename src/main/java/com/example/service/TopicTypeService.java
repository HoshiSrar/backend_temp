package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.TopicType;

import java.util.List;


/**
 * (Topic)表服务接口
 *
 * @author makejava
 * @since 2024-01-04 21:01:57
 */
public interface TopicTypeService extends IService<TopicType> {

    List<TopicType> listType();
}
