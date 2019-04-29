package com.contract.wechat.sgin.service.impl;

import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.CompanyDao;
import com.contract.wechat.sgin.entity.CompanyEntity;
import com.contract.wechat.sgin.service.CompanyService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service("companyService")
@ConfigurationProperties(prefix = "contract.wechat")
public class CompanyServiceImpl extends ServiceImpl<CompanyDao,CompanyEntity> implements CompanyService {
}
