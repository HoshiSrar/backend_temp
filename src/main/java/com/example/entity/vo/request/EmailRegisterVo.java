package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVo {
    @Email
    String mail;
    @Length(max = 6,min = 6)
    String code;
    @Pattern(regexp = "^[a-zA-z0-9\\u4e00-\\u9fa5]+$")
            @Length(max = 10)
    String username;
    @Length(min = 6,max = 20)
    String password;


}
