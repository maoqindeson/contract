package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_contract_file")
public class ContractFileEntity extends BaseEntity implements Serializable {
    private String name;
    private String path;
}
