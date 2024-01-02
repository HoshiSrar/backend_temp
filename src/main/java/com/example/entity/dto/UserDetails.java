package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
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
    
    private Integer gender;
    
    private String phone;
    
    private String qq;
    @TableField("wechat")
    private String wx;
    @TableField("`desc`")
    private String desc;
    
}
