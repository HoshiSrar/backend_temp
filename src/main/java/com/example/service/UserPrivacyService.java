package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.UserPrivacy;
import com.example.entity.vo.request.PrivacySaveVo;


/**
 * (Userprivacy)表服务接口
 *
 * @author makejava
 * @since 2024-01-02 09:04:29
 */
public interface UserPrivacyService extends IService<UserPrivacy> {
    void savePrivacy(int id , PrivacySaveVo vo);
    UserPrivacy userPrivacy(int id);
}
