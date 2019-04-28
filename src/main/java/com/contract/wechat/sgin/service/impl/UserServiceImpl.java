package com.contract.wechat.sgin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.UserDao;
import com.contract.wechat.sgin.entity.UserEntity;
import com.contract.wechat.sgin.service.UserService;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.DateUtil;
import com.contract.wechat.sgin.utils.ExcelUtil;
import com.contract.wechat.sgin.utils.ExportInfo;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import io.renren.modules.generator.dao.UserDao;

@Data
@Slf4j
@Service("userService")
@ConfigurationProperties(prefix = "contract.wechat")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
    private String exportfile;
    @Override
    public String exportCurrenPage2xls() {
        ExcelUtil excelUtil = new ExcelUtil();
        List<UserEntity> list = baseMapper.selectList(new EntityWrapper<UserEntity>());
        log.warn("查询到userformidlist为 : " + list.toString());
//        List<UserEntity> list = this.selectList(new EntityWrapper<UserEntity>());
        try {
            String filename = "/user" + DateUtil.getFormatDate(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            String filepath = exportfile + filename;
            Map<String, ExportInfo> header = new LinkedHashMap<String, ExportInfo>();
            header.put("username", new ExportInfo("员工姓名"));
            header.put("mobile", new ExportInfo("手机号码"));
            header.put("idCard", new ExportInfo("身份证"));
            header.put("department", new ExportInfo("部门"));
            header.put("createdAt", new ExportInfo("创建时间"));
            if (list != null && !list.isEmpty()) {
                excelUtil.export2XlsWithInfo(list, header, filepath);
                return filepath;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            return null;
        }

    }
    @Override
    public BaseResp importUsers(String fileName) {
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
                String mobile = excelUtil.getHSSTextString(row, 1);
                String idCard = excelUtil.getHSSTextString(row, 2);
                String department = excelUtil.getHSSTextString(row, 3);
                if (!"员工姓名".equalsIgnoreCase(name)) {
                    return BaseResp.error("表头不对,必须为员工姓名");
                }
                if (!"手机号码".equalsIgnoreCase(mobile)) {
                    return BaseResp.error("表头不对,必须为手机号码");
                }
                if (!"身份证".equalsIgnoreCase(idCard)) {
                    return BaseResp.error("表头不对,必须为身份证");
                }
                if (!"部门".equalsIgnoreCase(department)) {
                    return BaseResp.error("表头不对,必须为部门" );
                }
            }
            int success = 0;
            int faliure = 0;
            int mobileSame = 0;
            int idCardSame = 0;
            for (int i = 1; i < rows.size(); i++) {
                Row row = rows.get(i);
                //获取excel中第一列数据
                //第一行第一列
                String name = excelUtil.getHSSTextString(row, 0);
                String mobile = excelUtil.getHSSTextString(row, 1);
                String idCard = excelUtil.getHSSTextString(row, 2);
                String department = excelUtil.getHSSTextString(row, 3);
                if (StringTools.isNullOrEmpty(name)) {
                    return BaseResp.ok("员工姓名不能为空");
                }
                if (StringTools.isNullOrEmpty(mobile)) {
                    return BaseResp.ok("手机号码不能为空");
                }
                if (StringTools.isNullOrEmpty(idCard)) {
                    return BaseResp.ok("身份证不能为空");
                }
                if (StringTools.isNullOrEmpty(department)) {
                    return BaseResp.ok("部门不能为空");
                }
                //TODO 插入数据库
                UserEntity userEntity = new UserEntity();
                List<Object> list =baseMapper.selectObjs(new EntityWrapper<UserEntity>().eq("mobile",mobile));
                if(list!=null && !list.isEmpty()){
                    mobileSame++;
                    continue;
                }
                userEntity.setMobile(mobile);
                List<Object> list1 =baseMapper.selectObjs(new EntityWrapper<UserEntity>().eq("id_card",idCard));
                if(list1!=null && !list.isEmpty()){
                    idCardSame++;
                    continue;
                }
                userEntity.setIdCard(idCard);
                userEntity.setUsername(name);
                userEntity.setDepartment(department);
                userEntity.setCreatedAt(LocalDateTime.now());
                boolean result = this.insert(userEntity);
                if (!result) {
                    faliure++;
                    log.error("第" + i + "行数据插入失败");
                } else {
                    success++;
                }
            }
            return BaseResp.ok("已存在的手机用户"+mobileSame+"条数据,"+"已存在的身份证用户"+idCardSame+"条数据,"+
                    "成功插入" + success + "条数据,失败" + faliure + "条数据");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return BaseResp.error();
    }
}


