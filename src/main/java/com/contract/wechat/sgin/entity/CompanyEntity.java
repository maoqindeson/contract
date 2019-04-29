package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("tb_company")
public class CompanyEntity extends BaseEntity implements Serializable {
    private String name;
    private String code;
    private String legalPerson;
    private String address;
    private String contacts;
    private String telephone;
    private String status;
}
