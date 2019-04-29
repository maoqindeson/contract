package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_empolye")
public class EmpolyeEntity extends BaseEntity implements Serializable {
    private String name;
    private String gender;
    private String mobile;
    private String idCard;
}
