package com.example.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.experimental.Accessors;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * (Userprivacy)表实体类
 *
 * @author makejava
 * @since 2024-01-02 09:02:18
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_user_privacy")
public class UserPrivacy  {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private boolean phone =true;
    
    private boolean email =true;
    
    private boolean wx =true;
    
    private boolean qq =true;
    
    private boolean gender =true;

    /**
     * 获得用户隐私不需显示的字段
     * @return
     */
    public String[] hiddenFields(){
        List<String> hiddenStrings = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields){
            try{
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)){
                    hiddenStrings.add(field.getName());
                }
            }catch (Exception e){

            }
        }
        return hiddenStrings.toArray(String[]::new);
    }
    public UserPrivacy(int id){
        this.id = id;
    }


}

