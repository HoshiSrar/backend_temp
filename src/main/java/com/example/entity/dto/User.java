package com.example.entity.dto;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2023-12-22 05:04:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User  {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private String role;
    @TableField(fill = FieldFill.INSERT)
    private Date registerTime;



}

