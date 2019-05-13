package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("tb_contract")
public class ContractEntity extends BaseEntity implements Serializable {
    private String name;
    private String fileIds;
    private String serialNumber;
    private String company;
    private String agent;
    private String telephone;
    private String receiver;
    private LocalDateTime validity;
    private String note;
    private String status;
    @TableField(exist = false)
    private List<String> fileName;
    @TableField(exist = false)
    private EmpolyeEntity empolyeEntity;
}
