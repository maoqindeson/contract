package com.contract.wechat.sgin.service;

import com.baomidou.mybatisplus.service.IService;
import com.contract.wechat.sgin.entity.EmpolyeEntity;
import com.contract.wechat.sgin.utils.BaseResp;

public interface EmpolyeService extends IService<EmpolyeEntity> {
    BaseResp importEmpolye(String fileName);
    String exportCurrenPage2xls();
}
