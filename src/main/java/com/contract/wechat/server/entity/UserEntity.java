package com.contract.wechat.server.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_user")
public class UserEntity extends BaseEntity implements Serializable {
    private String username;
    private String mobile;
    private String idCard;
    private String department;
}
