package com.example.entity.dto;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * (TopicComment)表实体类
 *
 * @author makejava
 * @since 2024-01-08 19:55:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_topic_comment")
@Accessors(chain = true)
public class TopicComment  {
    @TableId(type = IdType.AUTO)
    private Integer id;

    
    private Integer uid;
    
    private Integer tid;
    
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private Date time;
    
    private Integer quote;



}

