package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_contract_alter")
public class ContractAlterEntity extends BaseEntity implements Serializable {
    private Integer contractId;
    private Integer userId;
    private String changeContent;
}
