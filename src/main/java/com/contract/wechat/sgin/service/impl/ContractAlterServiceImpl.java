package com.contract.wechat.sgin.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.ContractAlterDao;
import com.contract.wechat.sgin.entity.ContractAlterEntity;
import com.contract.wechat.sgin.service.ContractAlterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;


@Data
@Slf4j
@Service("contractAlterService")
@ConfigurationProperties(prefix = "contract.wechat")
public class ContractAlterServiceImpl extends ServiceImpl<ContractAlterDao, ContractAlterEntity> implements ContractAlterService {

}
