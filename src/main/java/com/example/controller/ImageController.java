package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.service.UserAvatarService;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@Slf4j
public class ImageController {

    @Resource
    UserAvatarService avatarService;
    @PostMapping("/avatar")
    public ResponseBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,
                                             @RequestAttribute(SystemConstants.ATTR_USER_ID)int id) throws IOException {
        if (file.getSize()>1024*100){
            return ResponseBean.failure(400, "头像不能大于100kb");
        }
        log.info("正在头像进行上传操作...");
        String path = avatarService.uploadImageById(id, file);
        log.info("头像上传完成...");
        return path == null ? ResponseBean.failure(400, "未知错误，上传失败"):ResponseBean.success(path);
    }

    @PostMapping("/cache")
    public ResponseBean<String> uploadImage(@RequestParam("file")MultipartFile file,
                                            HttpServletResponse response,
                                            @RequestAttribute(SystemConstants.ATTR_USER_ID)int id) throws IOException {
        if (file.getSize()>1024*1024*2){
            return ResponseBean.failure(400, "图片不能大于2Mb");
        }
        log.info("正在进行图片上传操作...");
        String path = avatarService.uploadImage(id, file);
        log.info("图片上传完成...");
        if(path == null) {
            response.setStatus(400);
            return ResponseBean.failure(400, "未知错误，上传失败");
        }else {
            return ResponseBean.success(path);
        }
    }
}
