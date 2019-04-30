package com.contract.wechat.sgin.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.ContractFileDao;
import com.contract.wechat.sgin.entity.ContractFileEntity;
import com.contract.wechat.sgin.service.ContractFileService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;


@Data
@Slf4j
@Service("contractFileService")
@ConfigurationProperties(prefix = "contract.wechat")
public class ContractFileServiceImpl extends ServiceImpl<ContractFileDao, ContractFileEntity> implements ContractFileService {

}
