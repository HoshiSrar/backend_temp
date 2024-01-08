package com.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddCommentVo {
    @Min(1)
    int tid;
    String content;
    //指向其他评论id
    @Min(-1)
    int quote;

}
