package com.example.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils(){}

    /**
     *  Bean 1 copy->Bean 2
     * */
    public static<V> V copyBean(Object source,Class<V> clazz) {

        V result = null;
        try {
            //        创建目标对象
            result = clazz.newInstance();
            //        实现属性copy
            BeanUtils.copyProperties(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //        返回结果
        return result;
    }
//  利用流，将List中的每一个都copy再收集然后返回
/**
 * 将 1号集合类型 的内容—> 2号中 返回2号类型List集合
 * */
    public static<O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());

    }
}
