package com.contract.wechat.sgin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.contract.wechat.sgin.entity.EmpolyeEntity;
import com.contract.wechat.sgin.form.EmployeeAll;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpolyeDao extends BaseMapper<EmpolyeEntity> {
    List<EmployeeAll> getEmployeeAll();
}
