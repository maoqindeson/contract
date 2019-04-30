package com.contract.wechat.sgin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.contract.wechat.sgin.entity.CompanyEntity;
import com.contract.wechat.sgin.service.CompanyService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Data
@Controller
@RequestMapping("/wechat/company")
@ConfigurationProperties(prefix = "contract.wechat")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @RequestMapping("/updateStatus")
    @ResponseBody
    public BaseResp updateStatus(String name,String status){
        if (StringTools.isNullOrEmpty(name)){
            log.warn("公司名字不能为空");
            return BaseResp.error("公司名字不能为空");
        }
        if (StringTools.isNullOrEmpty(status)){
            log.warn("状态不能为空");
            return BaseResp.error("状态不能为空");
        }
        try{
            CompanyEntity companyEntity = companyService.selectOne(new EntityWrapper<CompanyEntity>().eq("name",name));
            if (companyEntity == null){
                log.error("公司不存在");
                return BaseResp.error("公司不存在");
            }
            companyEntity.setStatus(status);
            boolean b = companyService.updateById(companyEntity);
            if (!b){
                log.error("改变状态失败");
                return BaseResp.error("改变状态失败");
            }
            return BaseResp.ok("改变状态成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新异常"+e);
        }
       return BaseResp.error("更新失败");
    }
}
