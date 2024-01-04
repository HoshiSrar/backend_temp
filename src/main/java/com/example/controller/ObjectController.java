package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ResponseBean;
import com.example.entity.dto.UserAvatar;
import com.example.service.UserAvatarService;
import com.example.utils.WebUtils;
import com.example.utils.constants.SystemConstants;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@Slf4j
public class ObjectController {
    @Resource
    UserAvatarService avatarService;
    @GetMapping("/images/**")
    public void imagesFetch(HttpServletRequest req, HttpServletResponse rsp) throws Exception{
        rsp.setHeader("Content-Type","image/jpg");
        log.info("获取图片中...");
        fetchImages(req, rsp);
        log.info("获取图片完成...");
    }
    @GetMapping("/image/userAvatar")
    public ResponseBean<UserAvatar> imagesFetchUrl(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        UserAvatar avatarURL = avatarService.getById(id);
        return ResponseBean.success(avatarURL);
    }



    private void fetchImages(HttpServletRequest req, HttpServletResponse rsp) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String imagesPath = req.getServletPath().substring(7);
        if (imagesPath.length()<13){
            WebUtils.renderString(rsp, ResponseBean.failure(400, "路径长度错误").asToJsonString());
        }else {
            avatarService.fetchImagesFromMinio(rsp.getOutputStream(), imagesPath);
            rsp.setHeader("CaChe-Control", "max-age=2592000 ");//默认缓存 1 个月

        }
    }
}
