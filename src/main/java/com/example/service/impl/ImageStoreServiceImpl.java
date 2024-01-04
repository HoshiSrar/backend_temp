package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.ImageStore;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageStoreService;
import org.springframework.stereotype.Service;


/**
 * (ImageStore)表服务实现类
 *
 * @author makejava
 * @since 2024-01-04 12:57:21
 */
@Service("imageStoreService")
public class ImageStoreServiceImpl extends ServiceImpl<ImageStoreMapper, ImageStore> implements ImageStoreService {

}
