package com.example.entity.vo.response;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

@Data
public class TopicPreviewVo {
    int id;
    int type;
    String title;
    //@Length()
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String username;
    String avatar;
    int like;
    int collect;
}
