package com.contract.wechat.server.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.contract.wechat.server.entity.ContractEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContractDao extends BaseMapper<ContractEntity> {

}
