package com.contract.wechat.sgin.form;

import lombok.Data;

@Data
public class WechatLoginForm {
    private String nickName;
    private String avatarUrl;
    private String gender;
    private String encryptedData;
    private String iv;
}
