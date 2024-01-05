package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * (Topic)表数据库访问层
 *
 * @author makejava
 * @since 2024-01-04 21:08:44
 */
public interface TopicMapper extends BaseMapper<Topic> {
        @Select("""
                select * from t_topic left join t_user on uid = t_user.id
                order by `time` desc limit ${start}, 10
                """)
        List<Topic> topicList(int start);

        @Select("""
                    select * from t_topic left join t_user on uid = t_user.id
                    where type = ${type}
                    order by `time` desc limit ${start}, 10
                    """)
        List<Topic> topicListByType(int start,int type);
}

