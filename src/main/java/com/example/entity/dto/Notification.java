package com.example.entity.dto;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Notification)表实体类
 *
 * @author makejava
 * @since 2024-01-09 00:04:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_notification")
public class Notification  {
    @TableId(type = IdType.AUTO)
    private Integer id;

    
    private Integer uid;
    
    private String title;
    
    private String content;
    
    private String type;
    
    private String url;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date time;



}

