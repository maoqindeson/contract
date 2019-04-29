package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_empolye_company")
public class EmpolyeCompanyEntity extends BaseEntity implements Serializable {
    private Integer empolyeId;
    private Integer companyId;
    private String type;
}
