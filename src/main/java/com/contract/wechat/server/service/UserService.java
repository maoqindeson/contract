package com.contract.wechat.server.service;

//import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.IService;
import com.contract.wechat.server.entity.UserEntity;
import com.contract.wechat.server.utils.BaseResp;

/**
 * 
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface UserService extends IService<UserEntity> {
    BaseResp importUsers(String fileName);
    String exportCurrenPage2xls();
}

