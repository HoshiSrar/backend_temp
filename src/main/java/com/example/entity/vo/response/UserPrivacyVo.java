package com.example.entity.vo.response;

import lombok.Data;

@Data
public class UserPrivacyVo {
    private boolean phone;

    private boolean email;

    private boolean wx;

    private boolean qq;

    private boolean gender;
}
