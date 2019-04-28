package com.contract.wechat.sgin.entity;

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
    private String openId;
    private String unionId;
    private String nickName;
    private String avatarUrl;
    private String gender;
}
