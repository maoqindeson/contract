package com.contract.wechat.sgin.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.EmpolyeCompanyDao;
import com.contract.wechat.sgin.dao.EmpolyeContractDao;
import com.contract.wechat.sgin.entity.EmpolyeCompanyEntity;
import com.contract.wechat.sgin.entity.EmpolyeContractEntity;
import com.contract.wechat.sgin.service.EmpolyeCompanyService;
import com.contract.wechat.sgin.service.EmpolyeContractService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service("empolyeContractService")
@ConfigurationProperties(prefix = "contract.wechat")
public class EmpolyeContractServiceImpl extends ServiceImpl<EmpolyeContractDao,EmpolyeContractEntity> implements EmpolyeContractService {
}
