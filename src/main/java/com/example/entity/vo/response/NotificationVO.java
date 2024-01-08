package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    Date time;
}
