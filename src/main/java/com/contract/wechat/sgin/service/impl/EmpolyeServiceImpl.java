package com.contract.wechat.sgin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.EmpolyeDao;
import com.contract.wechat.sgin.entity.CompanyEntity;
import com.contract.wechat.sgin.entity.EmpolyeCompanyEntity;
import com.contract.wechat.sgin.entity.EmpolyeEntity;
import com.contract.wechat.sgin.entity.UserEntity;
import com.contract.wechat.sgin.form.EmployeeAll;
import com.contract.wechat.sgin.service.CompanyService;
import com.contract.wechat.sgin.service.EmpolyeCompanyService;
import com.contract.wechat.sgin.service.EmpolyeService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.DateUtil;
import com.contract.wechat.sgin.utils.ExcelUtil;
import com.contract.wechat.sgin.utils.ExportInfo;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Slf4j
@Service("empolyeService")
@ConfigurationProperties(prefix = "contract.wechat")
public class EmpolyeServiceImpl extends ServiceImpl<EmpolyeDao, EmpolyeEntity> implements EmpolyeService {
    private String exportfile;
    @Autowired
    private EmpolyeService empolyeService;
    @Autowired
    private EmpolyeCompanyService empolyeCompanyService;
    @Autowired
    private CompanyService companyService;

    @Override
    public BaseResp importEmpolye(String fileName) {
        //新建一个excelUtil类
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            //获取excel中内容
            List<Row> rows = excelUtil.readExcel(fileName);
            if (rows.size() > 0) {
                //获取第一行(表头)
                Row row = rows.get(0);
                //第一行第一列
                String name = excelUtil.getHSSTextString(row, 0);
                //第一行第二列
                String gender = excelUtil.getHSSTextString(row, 1);
                String mobile = excelUtil.getHSSTextString(row, 2);
                String idCard = excelUtil.getHSSTextString(row, 3);
                if (!"员工姓名".equalsIgnoreCase(name)) {
                    return BaseResp.error("表头不对,必须为员工姓名");
                }
                if (!"性别".equalsIgnoreCase(gender)) {
                    return BaseResp.error("表头不对,必须为性别");
                }
                if (!"手机号码".equalsIgnoreCase(mobile)) {
                    return BaseResp.error("表头不对,必须为手机号码");
                }
                if (!"身份证".equalsIgnoreCase(idCard)) {
                    return BaseResp.error("表头不对,必须为身份证");
                }

            }
            int success = 0;
            int faliure = 0;
            int update = 0;
            for (int i = 1; i < rows.size(); i++) {
                Row row = rows.get(i);
                //获取excel中第一列数据
                //第一行第一列
                String name = excelUtil.getHSSTextString(row, 0);
                String gender = excelUtil.getHSSTextString(row, 1);
                String mobile = excelUtil.getHSSTextString(row, 2);
                String idCard = excelUtil.getHSSTextString(row, 3);
                String companyId = excelUtil.getHSSTextString(row, 4);
                if (StringTools.isNullOrEmpty(name)) {
                    log.error("第" + i + "行的员工姓名为空");
                    continue;
                }
                if (StringTools.isNullOrEmpty(mobile)) {
                    log.error("第" + i + "行的手机号码为空");
                    continue;
                }
                if (StringTools.isNullOrEmpty(idCard)) {
                    log.error("第" + i + "行的身份证为空");
                    continue;
                }
                if (StringTools.isNullOrEmpty(gender)) {
                    log.error("第" + i + "行的手机号码姓名为空");
                    continue;
                }
                //TODO 插入数据库
                EmpolyeEntity empolyeEntity = empolyeService.selectOne(new EntityWrapper<EmpolyeEntity>().eq("id_card", idCard));
                Integer empolyeId = 0;
                if (empolyeEntity != null) {
                    empolyeEntity.setMobile(mobile);
                    empolyeEntity.setName(name);
                    empolyeEntity.setGender(gender);
                    empolyeEntity.setUpdatedAt(LocalDateTime.now());
                    boolean res = empolyeService.updateById(empolyeEntity);
                    if (!res) {
                        faliure++;
                        log.error("更新失败");
                    } else {
                        update++;
                    }
                    empolyeId = empolyeEntity.getId();
                    EmpolyeCompanyEntity empolyeCompanyEntity= empolyeCompanyService.selectOne(new EntityWrapper<EmpolyeCompanyEntity>().eq("id",empolyeId));
                    if(empolyeCompanyEntity!=null){
                        empolyeCompanyEntity.setCompanyId(Integer.parseInt(companyId));
                        empolyeCompanyEntity.setUpdatedAt(LocalDateTime.now());
                        boolean b = empolyeCompanyService.updateById(empolyeCompanyEntity);
                        if (!b) {
                            log.error("插入员工公司关系表失败");
                            return BaseResp.error("插入员工公司关系表失败");
                        }
                    }
                } else {
                    EmpolyeEntity empolyeEntity1 = new EmpolyeEntity();
                    empolyeEntity1.setMobile(mobile);
                    empolyeEntity1.setName(name);
                    empolyeEntity1.setGender(gender);
                    empolyeEntity1.setIdCard(idCard);
                    empolyeEntity1.setCreatedAt(LocalDateTime.now());
                    boolean result = this.insert(empolyeEntity1);
                    if (!result) {
                        faliure++;
                        log.error("第" + i + "行数据插入失败");
                    } else {
                        success++;
                    }
                    empolyeId = empolyeEntity1.getId();
                    EmpolyeCompanyEntity empolyeCompanyEntity = new EmpolyeCompanyEntity();
                    empolyeCompanyEntity.setEmpolyeId(empolyeId);
                    empolyeCompanyEntity.setCompanyId(Integer.parseInt(companyId));
                    empolyeCompanyEntity.setCreatedAt(LocalDateTime.now());
                    boolean b = empolyeCompanyService.insert(empolyeCompanyEntity);
                    if (!b) {
                        log.error("插入员工公司关系表失败");
                        return BaseResp.error("插入员工公司关系表失败");
                    }
                }
            }
            return BaseResp.ok("成功更新" + update + "条数据,失败" + faliure + "条数据,"+
                    "成功插入" + success + "条数据,失败" + faliure + "条数据");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return BaseResp.error();
    }


    @Override
    public String exportCurrenPage2xls() {
        ExcelUtil excelUtil = new ExcelUtil();
        List<EmployeeAll> list = baseMapper.getEmployeeAll();
        for(EmployeeAll employeeAll:list){
            employeeAll.setCreatedAt(LocalDateTime.now());
        }
        log.warn("查询到employeAllformidlist为 : " + list.toString());
        try {
            String filename = "/empolyeAll" + DateUtil.getFormatDate(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            String filepath = exportfile + filename;
            Map<String, ExportInfo> header = new LinkedHashMap<String, ExportInfo>();
            header.put("empolyeName", new ExportInfo("员工姓名"));
            header.put("gender", new ExportInfo("性别"));
            header.put("mobile", new ExportInfo("手机号码"));
            header.put("idCard", new ExportInfo("身份证"));
            header.put("companyName", new ExportInfo("公司名"));
            header.put("code", new ExportInfo("营业执照"));
            header.put("legalPerson", new ExportInfo("法人"));
            header.put("address", new ExportInfo("经营地址"));
            header.put("contacts", new ExportInfo("公司联系人"));
            header.put("telephone", new ExportInfo("公司联系电话"));
            header.put("companyStatus", new ExportInfo("公司状态"));
            header.put("createdAt", new ExportInfo("创建时间"));
            if (list != null && !list.isEmpty()) {
                excelUtil.export2XlsWithInfo(list, header, filepath);
                return filepath;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
        return null;
    }
}
