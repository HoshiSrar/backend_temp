package com.example.entity.dto;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.experimental.Accessors;

/**
 * (UserAvatar)表实体类
 *
 * @author makejava
 * @since 2024-01-03 10:17:50
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_user_avatar")
public class UserAvatar  {

    
    private Integer id;
    
    private String avatar;



}

