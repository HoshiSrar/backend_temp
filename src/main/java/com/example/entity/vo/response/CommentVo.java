package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class CommentVo {

    private Integer id;
    private String content;
    private Date time;
    private String quote;
    private User user;

    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
        boolean gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
