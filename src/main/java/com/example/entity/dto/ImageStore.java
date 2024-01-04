package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * (ImageStore)表实体类
 *
 * @author makejava
 * @since 2024-01-04 12:57:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_image_store")
public class ImageStore  {
    
    private Integer uid;
    
    private String name;
    
    private Date time;



}

