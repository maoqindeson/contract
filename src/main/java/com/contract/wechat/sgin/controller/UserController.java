package com.contract.wechat.sgin.controller;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.contract.wechat.sgin.aop.WebRecord;
import com.contract.wechat.sgin.entity.UserEntity;
import com.contract.wechat.sgin.form.WechatLoginForm;
import com.contract.wechat.sgin.service.UserService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.wechat.JWTUtil;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import com.sun.xml.internal.rngom.parse.host.Base;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sf.json.JSONObject.fromObject;

@Data
@Slf4j
@RestController
@RequestMapping("/wechat")
@Api(value = "微信相关接口", tags = {"微信相关接口"})
@ConfigurationProperties(prefix = "contract.wechat")
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;

    private String appId;//微信小程序appid
    private String appSecret;//微信小程序密钥
    private String grantType;
    private String mchId;
    private String withdrawMchId;
    private String notifyUrl;
    private String TRADETYPE;
    private String key;
    private String withdrawKey;
    private String host;
    private String certPath;
    private String sendRedPacketUrl;
    private String wxOfficialAppId;

    @WebRecord
    @GetMapping("/getUserByMobile")
    public BaseResp getUserByMobile(String mobile) {
        if (StringTools.isNullOrEmpty(mobile)) {
            log.warn("手机号码不能为空");
            return BaseResp.error("手机号码不能为空");
        }
        try {
            UserEntity userEntity = userService.selectOne(new EntityWrapper<UserEntity>().eq("mobile", mobile));
            if (userEntity == null) {
                log.error("查不到用户");
                return BaseResp.error("查不到用户");
            }
            log.info("查询成功" + userEntity);
            return BaseResp.ok(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询异常" + e);
        }
        return BaseResp.error("查询失败");
    }

    @WebRecord
    @GetMapping("/deleteUserByMobile")
    public BaseResp deleteUserByMobile(String mobile) {
        if (StringTools.isNullOrEmpty(mobile)) {
            log.warn("手机号不能为空,手机号是" + mobile);
            return BaseResp.error("手机号不能为空");
        }
        try {
            boolean bl = userService.delete(new EntityWrapper<UserEntity>().eq("mobile", mobile));
            if (bl == false) {
                log.error("删除失败,请重试");
                return BaseResp.error("删除失败,请重试");
            }
            return BaseResp.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除异常" + e);
        }
        return BaseResp.error("删除失败");
    }

    @WebRecord
    @PostMapping("/insert")
    public BaseResp insert(String username, String mobile, String idCard, String department) {
        if (StringTools.isNullOrEmpty(username)) {
            log.warn("用户名不能空");
            return BaseResp.error("用户名不能空");
        }
        if (StringTools.isNullOrEmpty(mobile)) {
            log.warn("手机号不能空");
            return BaseResp.error("手机号不能空");
        }
        if (StringTools.isNullOrEmpty(idCard)) {
            log.warn("身份证不能空");
            return BaseResp.error("身份证不能空");
        }
        if (StringTools.isNullOrEmpty(department)) {
            log.warn("部门不能空");
            return BaseResp.error("部门不能空");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setMobile(mobile);
        userEntity.setIdCard(idCard);
        userEntity.setDepartment(department);
        try {
            boolean bl = userService.insert(userEntity);
            if (bl == false) {
                log.error("插入失败");
                return BaseResp.error("插入失败");
            }
            return BaseResp.ok("插入成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("插入异常");
        }
        return BaseResp.error("插入失败");
    }


    @WebRecord
    @PostMapping("login")
    public BaseResp login(@RequestBody WechatLoginForm wechatLoginForm, String code) {
        String avatarUrl = wechatLoginForm.getAvatarUrl();
        String gender = wechatLoginForm.getGender();
        String nickName = wechatLoginForm.getNickName();
        //拼接参数
        String param = "?grant_type=" + grantType + "&appid=" + appId + "&secret=" + appSecret + "&js_code=" + code;
        String url = "https://api.weixin.qq.com/sns/jscode2session" + param;
        //get json数据
        String result = restTemplate.getForEntity(url, String.class).getBody();
        if (StringTools.isNullOrEmpty(result)) {
            log.error("小程序登录接口返回结果为空");
            return BaseResp.error("小程序登录接口返回结果为空");
        }
        JSONObject rsJosn = fromObject(result);
        if (rsJosn.get("openid") == null) {
            log.error("小程序登录接口无法获得openid");
            return BaseResp.error("小程序登录接口无法获得openid");
        }
        String openId = rsJosn.get("openid").toString();
        //如果通过open_id能查出存在用户，则直接返回用户信息
        synchronized (this) {
            if (0 == userService.selectCount(new EntityWrapper<UserEntity>().eq("open_id", openId))) {
                //抽空不全插入检查，唯一键等；
                UserEntity userEntity = new UserEntity();
                userEntity.setOpenId(openId);
                userEntity.setAvatarUrl(avatarUrl);
                userEntity.setNickName(nickName);
                userEntity.setGender(gender);
                userEntity.setCreatedAt(LocalDateTime.now());
                boolean bl = userService.insert(userEntity);
                if (!bl) {
                    log.error("登陆接口插入用户数据失败,用户openid为" + openId + "昵称为" + nickName);
                    return BaseResp.error("登陆接口插入用户数据失败,用户openid为" + openId + "昵称为" + nickName);
                }
            }
        }
        String token = JWTUtil.sign(openId);
        Map<String, Object> map = new HashMap<>();
        map.put("openId", openId);
        map.put("token", token);
        return BaseResp.ok(map);
    }

    //微信授权登录后的手机号码登录
    @WebRecord
    @PostMapping("mobileLogin")
    @RequiresAuthentication
    public BaseResp mobileLogin(String mobile, String verifycode, HttpServletRequest request) {
        //判断验证码是否正确
        String openId = JWTUtil.getCurrentUsername(request);
        Integer i = userService.selectCount(new EntityWrapper<UserEntity>().eq("open_id", openId));
        if (i == 0) {
            log.error("没有您的合同");
            return BaseResp.error("没有您的合同");
        }
        String token = JWTUtil.sign(openId);
        Map<String, Object> map = new HashMap<>();
        map.put("openId", openId);
        map.put("token", token);
        return BaseResp.ok("登陆成功", map);
    }
}
