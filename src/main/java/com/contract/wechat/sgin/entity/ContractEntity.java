package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_contract")
public class ContractEntity extends BaseEntity implements Serializable {
    private String contractName;
    private String content;


}
