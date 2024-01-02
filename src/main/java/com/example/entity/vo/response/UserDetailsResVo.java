package com.example.entity.vo.response;

import lombok.Data;

@Data
public class UserDetailsResVo {
    int gender;
    String phone;
    String qq;
    String wx;
    private String desc;

}
