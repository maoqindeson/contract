package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_empolye_contract")
public class EmpolyeContractEntity extends BaseEntity implements Serializable {
    private Integer empolyeId;
    private Integer contractId;
}
