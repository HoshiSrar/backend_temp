package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * (Topic)表数据库访问层
 *
 * @author makejava
 * @since 2024-01-04 21:08:44
 */
public interface TopicMapper extends BaseMapper<Topic> {
        @Insert("""
                <script>
                    insert ignore into t_topic_interact_${type} values
                    <foreach collection ="interacts" item="item" separator =",">
                        (#{item.tid}, #{item.uid}, #{item.time})
                    </foreach>
                </script>
                """)
        void addInteract(List<Interact> interacts, String type);

        @Delete("""
                <script>
                    delete from t_topic_interact_${type} where
                    <foreach collection="interacts" item="item" separator=" or ">
                        (tid = #{item.tid} and uid = #{item.uid})
                    </foreach>
                </script>
                """)
        int deleteInteract(List<Interact> interacts, String type);

        @Select("""
                select count(*) from t_topic_interact_${type} where tid = #{tid}
                """)
        int interactCount(int tid, String type);

        @Select("""
                select count(*) from t_topic_interact_${type} where tid = #{tid} and uid = #{uid}
                """)
        int userInteractCount(int tid, int uid, String type);

        /**
         * 查询 uid 收藏的 topic
         * @param id
         * @return
         */
        @Select("""
                select * from t_topic_interact_collect left join t_topic on tid = T_topic.id
                 where t_topic_interact_collect.uid = #{uid}
                """)
        List<Topic> collectTopics(int id);
}

