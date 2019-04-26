package com.contract.wechat.server.form;

import lombok.Data;

@Data
public class WechatLoginForm {
    private String nickName;
    private String avatarUrl;
    private String gender;
    private String encryptedData;
    private String iv;
}
