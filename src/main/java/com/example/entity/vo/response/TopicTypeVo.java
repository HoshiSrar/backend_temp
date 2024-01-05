package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class TopicTypeVo {

    int id;
    String name;
    String desc;
    String color;
}
