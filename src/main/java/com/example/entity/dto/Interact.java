package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Interact {
    Integer tid;
    Integer uid;
    Date time;
    String type;
    public String toKey(){
        return tid + ":" + uid;
    }

    /**
     * 将 hash 数据的 String 形式转为 Interact 对象
     * @param str hash 对象的 String 格式
     * @param type 本次 Interact 的操作类型
     * @return
     */
    public static Interact parseInteract(String str,String type){
        String[] key =str.split(":");
        return new Interact(Integer.parseInt(key[0]),Integer.parseInt(key[1]), new Date(), type);

    }
}
