package com.example.service.impl;

import com.alibaba.fastjson2.util.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.ImageStore;
import com.example.service.ImageStoreService;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import com.example.entity.dto.UserAvatar;
import com.example.mapper.UserAvatarMapper;
import com.example.service.UserAvatarService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

/**
 * (UserAvatar)表服务实现类
 *
 * @author makejava
 * @since 2024-01-03 10:17:50
 */
@Service("userAvatarService")
@Slf4j
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar> implements UserAvatarService {
    @Resource
    MinioClient minioClient;
    @Resource
    FlowUtils flowUtils;
    @Resource
    ImageStoreService  imageStoreService;
    @Override
    public String uploadImageById(int id, MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String imageName = UUID.randomUUID().toString().replace("-", "")+file.getOriginalFilename();
        imageName = "/avatar/"+imageName;
        PutObjectArgs putArgs = PutObjectArgs.builder()
                .bucket("test")
                .object(imageName)
                .stream(inputStream, file.getSize(), -1)
                .build();
        try{
            String OldAvatarUrl = getById(id).getAvatar();
            if(saveOrUpdate(new UserAvatar().setId(id).setAvatar(imageName))){
                minioClient.putObject(putArgs);
                deleteOldImage(OldAvatarUrl);
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.info("上传头像图片出现异常，{}",e.getMessage());
            return null;
        }

    }

    @Override
    public String uploadImage(int id, MultipartFile file) throws IOException {
        String key = SystemConstants.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key,20,3600)){
            return null;
        }
        Date date = new Date();
        String imageName = UUID.randomUUID().toString().replace("-", "")+file.getOriginalFilename();
        imageName = "/cache/"+DateUtils.format(date, "yyyyMMdd")+"/"+imageName;
        PutObjectArgs putArgs = PutObjectArgs.builder()
                .bucket("test")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try{
            minioClient.putObject(putArgs);
            if(imageStoreService.save(new ImageStore(id, imageName, date))){
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.info("上传图片出现异常，{}",e.getMessage());
            return null;
        }
    }

    @Override
    public void fetchImagesFromMinio(OutputStream stream, String imagePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("test")
                .object(imagePath)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, stream);
    }
    // 根据图片 url 删除 minio 中数据
    private void deleteOldImage(String imageUrl) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket("test")
                .object(imageUrl)
                .build();
        minioClient.removeObject(args);
    }
}
