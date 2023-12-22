package com.example.entity.vo.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserVo {
    String userName;
    String role;
    String token;
    Date expire;

}
