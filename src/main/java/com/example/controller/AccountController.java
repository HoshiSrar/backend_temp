package com.example.controller;

import com.example.entity.ResponseBean;
import com.example.entity.dto.User;
import com.example.entity.vo.response.AccountVo;
import com.example.service.UserService;
import com.example.utils.BeanCopyUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class AccountController {
    @Resource
    UserService userService;

    @GetMapping("/info")
    public ResponseBean<AccountVo> info(@RequestAttribute(SystemConstants.ATTR_USER_ID) int id){
        User user = userService.getById(id);
        AccountVo vo = BeanCopyUtils.copyBean(user, AccountVo.class);
        return ResponseBean.success(vo);

    }
}
