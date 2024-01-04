package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PrivacySaveVo {
    @Pattern(regexp = "(phone|email|qq|wx|gender)")
    String type;
    boolean status;



}
