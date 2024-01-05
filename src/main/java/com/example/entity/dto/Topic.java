package com.example.entity.dto;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Topic)表实体类
 *
 * @author makejava
 * @since 2024-01-04 21:01:57
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_topic")
public class Topic  {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private Integer uid;
    private String type;
    @TableField(fill = FieldFill.INSERT)
    private Date time;

}

