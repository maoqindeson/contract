package com.contract.wechat.sgin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.contract.wechat.sgin.aop.WebRecord;
import com.contract.wechat.sgin.entity.CompanyEntity;
import com.contract.wechat.sgin.entity.EmpolyeCompanyEntity;
import com.contract.wechat.sgin.entity.EmpolyeEntity;
import com.contract.wechat.sgin.service.CompanyService;
import com.contract.wechat.sgin.service.EmpolyeCompanyService;
import com.contract.wechat.sgin.service.EmpolyeService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Controller
@RequestMapping("/wechat/company")
@ConfigurationProperties(prefix = "contract.wechat")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EmpolyeCompanyService empolyeCompanyService;
    @Autowired
    private EmpolyeService empolyeService;
    @PostMapping("/updateCompany")
    @ResponseBody
    public BaseResp updateCompany(Integer id,String name,String code,String legalPerson,String address,String contacts,String telephone,String status){
        if (id==null || id==0){
            log.warn("请输入公司id");
            return BaseResp.error("请输入公司id");
        }
        try{
            CompanyEntity companyEntity = companyService.selectOne(new EntityWrapper<CompanyEntity>().eq("id",id));
            if (companyEntity==null){
                log.error("公司不存在");
                return BaseResp.error("公司不存在");
            }
            companyEntity.setName(name);
            companyEntity.setCode(code);
            companyEntity.setLegalPerson(legalPerson);
            companyEntity.setAddress(address);
            companyEntity.setContacts(contacts);
            companyEntity.setTelephone(telephone);
            companyEntity.setStatus(status);
            companyEntity.setUpdatedAt(LocalDateTime.now());
            boolean result = companyService.updateById(companyEntity);
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

    @PostMapping("/insertCompany")
    @ResponseBody
    public BaseResp insertCompany(String name,String code,String legalPerson,String address,String contacts,String telephone,String status){
        if (StringTools.isNullOrEmpty(name)){
            log.warn("名字不能为空");
            return BaseResp.error("名字不能为空");
        }
        if (StringTools.isNullOrEmpty(code)){
            log.warn("营业执照号不能为空");
            return BaseResp.error("营业执照号不能为空");
        }
        if (StringTools.isNullOrEmpty(legalPerson)){
            log.warn("法人不能为空");
            return BaseResp.error("法人不能为空");
        }
        if (StringTools.isNullOrEmpty(address)){
            log.warn("地址不能为空");
            return BaseResp.error("地址不能为空");
        }
        if (StringTools.isNullOrEmpty(contacts)){
            log.warn("合同不能为空");
            return BaseResp.error("合同不能为空");
        }
        if (StringTools.isNullOrEmpty(telephone)){
            log.warn("联系电话不能为空");
            return BaseResp.error("联系电话不能为空");
        }
        if (StringTools.isNullOrEmpty(status)){
            log.warn("状态不能为空");
            return BaseResp.error("状态不能为空");
        }
        try {
            CompanyEntity companyEntity1 = companyService.selectOne(new EntityWrapper<CompanyEntity>().eq("name",name));
            if (companyEntity1!=null){
                log.error("公司已存在");
                return BaseResp.error("公司已存在");
            }
            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setName(name);
            companyEntity.setCode(code);
            companyEntity.setLegalPerson(legalPerson);
            companyEntity.setAddress(address);
            companyEntity.setContacts(contacts);
            companyEntity.setTelephone(telephone);
            companyEntity.setStatus(status);
            companyEntity.setCreatedAt(LocalDateTime.now());
            boolean result = companyService.insert(companyEntity);
            if (!result){
                log.error("插入失败,请重试");
                return BaseResp.error("插入失败,请重试");
            }
            return BaseResp.ok("插入成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("插入异常");
        }
        return BaseResp.error("插入失败");
    }


    @GetMapping("/empoyleList")
    @ResponseBody
    public BaseResp empoyleList(String companyId) {
        if (StringTools.isNullOrEmpty(companyId)) {
            log.warn("公司Id不能为空");
            return BaseResp.error("公司Id不能为空");
        }
        try {
            List<EmpolyeCompanyEntity> list = empolyeCompanyService.selectList(new EntityWrapper<EmpolyeCompanyEntity>().eq("company_id", companyId));
            if (list == null || list.isEmpty()) {
                log.error("查询公司不存在");
                return BaseResp.error("查询公司不存在");
            }
            List<Integer> empolyeIdList = new ArrayList<>();
            for (EmpolyeCompanyEntity empolyeCompanyEntity : list) {
                empolyeIdList.add(empolyeCompanyEntity.getEmpolyeId());
            }
            List<EmpolyeEntity> empolyeList = empolyeService.selectBatchIds(empolyeIdList);
            if (empolyeList == null || empolyeList.isEmpty()) {
                log.error("查询员工失败");
                return BaseResp.error("查询员工失败");
            }
            return BaseResp.ok("查询成功", empolyeList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询异常");
        }
        return BaseResp.error("查询失败");
    }

    @RequestMapping("/updateStatus")
    @ResponseBody
    @WebRecord
    public BaseResp updateStatus(String name, String status) {
        if (StringTools.isNullOrEmpty(name)) {
            log.warn("公司名字不能为空");
            return BaseResp.error("公司名字不能为空");
        }
        if (StringTools.isNullOrEmpty(status)) {
            log.warn("状态不能为空");
            return BaseResp.error("状态不能为空");
        }
        try {
            CompanyEntity companyEntity = companyService.selectOne(new EntityWrapper<CompanyEntity>().eq("name", name));
            if (companyEntity == null) {
                log.error("公司不存在");
                return BaseResp.error("公司不存在");
            }
            companyEntity.setStatus(status);
            boolean b = companyService.updateById(companyEntity);
            if (!b) {
                log.error("改变状态失败");
                return BaseResp.error("改变状态失败");
            }
            return BaseResp.ok("改变状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新异常" + e);
        }
        return BaseResp.error("更新失败");
    }
}
