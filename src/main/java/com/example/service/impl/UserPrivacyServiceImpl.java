package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.UserPrivacy;
import com.example.entity.vo.request.PrivacySaveVo;
import com.example.mapper.UserPrivacyMapper;
import com.example.service.UserPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * (Userprivacy)表服务实现类
 *
 * @author makejava
 * @since 2024-01-02 09:04:29
 */
@Service("userprivacyService")
public class UserPrivacyServiceImpl extends ServiceImpl<UserPrivacyMapper, UserPrivacy> implements UserPrivacyService {

    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVo vo) {
        UserPrivacy privacy = Optional.ofNullable(getById(id)).orElse(new UserPrivacy().setId(id));
        boolean status = vo.isStatus();
        switch (vo.getType()){
            case "phone" -> privacy.setPhone(status);
            case "email" -> privacy.setEmail(status);
            case "gender" -> privacy.setGender(status);
            case "wx" -> privacy.setWx(status);
            case "qq" -> privacy.setQq(status);
        }
        saveOrUpdate(privacy);
    }
    @Override
    public UserPrivacy userPrivacy(int id){
        return Optional.ofNullable(getById(id)).orElse(new UserPrivacy().setId(id));
    }
}
