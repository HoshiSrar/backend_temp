package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.entity.vo.response.NotificationVO;
import com.example.service.NotificationService;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService service;

    @GetMapping("/list")
    public ResponseBean<List<NotificationVO>> listNotification(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id) {
        return ResponseBean.success(service.findUserNotification(id));
    }

    @GetMapping("/delete")
    public ResponseBean<List<NotificationVO>> deleteNotification(@RequestParam @Min(0) int id,
                                                             @RequestAttribute(SystemConstants.ATTR_USER_ID) int uid) {
        service.deleteUserNotification(id, uid);
        return ResponseBean.success();
    }

    @GetMapping("/delete-all")
    public ResponseBean<List<NotificationVO>> deleteAllNotification(@RequestAttribute(SystemConstants.ATTR_USER_ID) int uid) {
        service.deleteUserAllNotification(uid);
        return ResponseBean.success();
    }
}
