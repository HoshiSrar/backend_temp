package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_topic_type")
public class TopicType {
    @TableId
    int id;
    String name;
    @TableField("`desc`")
    String desc;
    String color;
}
