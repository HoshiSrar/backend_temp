package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVo {
    String username;
    String email;
    String role;
    Date registerTime;
}
