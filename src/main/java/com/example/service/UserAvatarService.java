package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.UserAvatar;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * (UserAvatar)表服务接口
 *
 * @author makejava
 * @since 2024-01-03 10:17:50
 */
public interface UserAvatarService extends IService<UserAvatar> {
    /**
     * 缓存式、受限式、上传头像，
     * @param id
     * @param file
     * @return 图片 url，失败返回 null
     * @throws IOException
     */
    String uploadImageById(int id, MultipartFile file) throws IOException;

    /**
     * 缓存式、受限式、上传图片，
     * @param id
     * @param file
     * @return 图片 url，失败返回 null
     * @throws IOException
     */
    String uploadImage(int id, MultipartFile file) throws IOException;

    /**
     * 从minio中读取头像到输出流中
     * @param stream
     * @param imagePath
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    void fetchImagesFromMinio(OutputStream stream , String imagePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;


}
