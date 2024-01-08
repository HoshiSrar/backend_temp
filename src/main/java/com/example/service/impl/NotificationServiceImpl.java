package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.vo.response.NotificationVO;
import com.example.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import com.example.entity.dto.Notification;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;

import java.util.List;

/**
 * (Notification)表服务实现类
 *
 * @author makejava
 * @since 2024-01-09 00:04:51
 */
@Service("notificationService")
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid))
                .stream()
                .map(notification -> BeanCopyUtils.copyBean(notification, NotificationVO.class))
                .toList();
    }

    public void deleteUserNotification(int id, int uid){
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    public void deleteUserAllNotification(int uid){
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        this.save(notification);
    }
}
