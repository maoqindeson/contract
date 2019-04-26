package com.contract.wechat.server.service;

import com.baomidou.mybatisplus.service.IService;
import com.contract.wechat.server.entity.ContractEntity;
import com.contract.wechat.server.utils.BaseResp;

public interface ContractService extends IService<ContractEntity> {
    BaseResp importUsers(String fileName);
    String exportCurrenPage2xls();
}
