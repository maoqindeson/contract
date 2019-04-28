package com.contract.wechat.sgin.service;

import com.baomidou.mybatisplus.service.IService;
import com.contract.wechat.sgin.entity.ContractEntity;
import com.contract.wechat.sgin.utils.BaseResp;

public interface ContractService extends IService<ContractEntity> {
    BaseResp importUsers(String fileName);
    String exportCurrenPage2xls();
}
