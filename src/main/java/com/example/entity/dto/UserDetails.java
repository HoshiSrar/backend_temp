package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user_details")
public class UserDetails {
    @TableId
    private Integer id;

    private String username;
    
    private Integer gender;
    
    private String phone;
    
    private String qq;
    
    private String wechat;
    
    private String descript;
    
}
