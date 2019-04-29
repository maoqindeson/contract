package com.contract.wechat.sgin.form;

import com.contract.wechat.sgin.entity.BaseEntity;
import lombok.Data;

@Data
public class EmployeeAll extends BaseEntity {
    private String empolyeName;
    private String gender;
    private String mobile;
    private String idCard;
    private String companyName;
    private String code;
    private String legalPerson;
    private String address;
    private String contacts;
    private String telephone;
    private String companyStatus;
}
