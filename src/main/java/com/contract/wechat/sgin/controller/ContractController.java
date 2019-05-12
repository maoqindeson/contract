package com.contract.wechat.sgin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.contract.wechat.sgin.aop.WebRecord;
import com.contract.wechat.sgin.entity.ContractEntity;
import com.contract.wechat.sgin.service.ContractService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Data
@Controller
@RequestMapping("/wechat/contract")
@ConfigurationProperties(prefix = "contract.wechat")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @PostMapping("/updateContract")
    @ResponseBody
    public BaseResp updateContract(Integer id,String name, String fileIds, String company, String agent, String telephone, String receiver, Long validity,String note,String status){
        if (id==null || id==0){
            log.warn("合同id不能为空");
            return BaseResp.error("合同id不能为空");
        }
        try {
            ContractEntity contractEntity = contractService.selectOne(new EntityWrapper<ContractEntity>().eq("id",id));
            if (contractEntity==null){
                log.error("合同不存在");
                return BaseResp.error("合同不存在");
            }
            if (validity!=null){
                Instant instant = Instant.ofEpochMilli(validity);
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime newvalidity =LocalDateTime.ofInstant(instant, zone);
                contractEntity.setValidity(newvalidity);
            }
            contractEntity.setName(name);
            contractEntity.setFileIds(fileIds);
            contractEntity.setCompany(company);
            contractEntity.setAgent(agent);
            contractEntity.setTelephone(telephone);
            contractEntity.setReceiver(receiver);
            contractEntity.setNote(note);
            contractEntity.setStatus(status);
            contractEntity.setUpdatedAt(LocalDateTime.now());
            boolean result = contractService.updateById(contractEntity);
            if (!result){
                log.error("更新失败");
                return BaseResp.error("更新失败");
            }
            return BaseResp.ok("更新成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新异常");
        }
        return BaseResp.error("更新失败");
    }

    @WebRecord
    @PostMapping("/insertContract")
    @ResponseBody
    public BaseResp insertContract(String name, String fileIds, String company, String agent, String telephone, String receiver, Long validity,String note,String status){
        if(StringTools.isNullOrEmpty(name)){
            log.warn("合同名不能为空");
            return BaseResp.error("合同名不能为空");
        }
        if(StringTools.isNullOrEmpty(fileIds)){
            log.warn("文件ID不能为空");
            return BaseResp.error("文件ID不能为空");
        }
        if(StringTools.isNullOrEmpty(company)){
            log.warn("公司名不能为空");
            return BaseResp.error("公司名不能为空");
        }
        if(StringTools.isNullOrEmpty(agent)){
            log.warn("经办人不能为空");
            return BaseResp.error("经办人不能为空");
        }
        if(StringTools.isNullOrEmpty(telephone)){
            log.warn("联系电话不能为空");
            return BaseResp.error("联系电话不能为空");
        }
        if(StringTools.isNullOrEmpty(receiver)){
            log.warn("接收方不能为空");
            return BaseResp.error("接收方不能为空");
        }
        if(validity == null || validity==0){
            log.warn("有效期不能为空");
            return BaseResp.error("有效期不能为空");
        }
        if(StringTools.isNullOrEmpty(status)){
            log.warn("状态不能为空");
            return BaseResp.error("状态不能为空");
        }
        try{
            ContractEntity contractEntity1 = contractService.selectOne(new EntityWrapper<ContractEntity>().eq("name",name));
            if (contractEntity1!=null){
                log.error("合同存在");
                return BaseResp.error("合同存在");
            }
            Instant instant = Instant.ofEpochMilli(validity);
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime newvalidity =LocalDateTime.ofInstant(instant, zone);
            ContractEntity contractEntity = new ContractEntity();
            contractEntity.setName(name);
            contractEntity.setFileIds(fileIds);
            contractEntity.setCompany(company);
            contractEntity.setAgent(agent);
            contractEntity.setTelephone(telephone);
            contractEntity.setReceiver(receiver);
            contractEntity.setValidity(newvalidity);
            contractEntity.setNote(note);
            contractEntity.setStatus(status);
            contractEntity.setCreatedAt(LocalDateTime.now());
            boolean b = contractService.insert(contractEntity);
            if(!b){
                log.error("保存失败");
                return BaseResp.error("保存失败");
            }
            return BaseResp.ok("保存成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存失败"+e);
        }
        return BaseResp.ok("保存失败");
    }
}
