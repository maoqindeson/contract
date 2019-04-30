package com.contract.wechat.sgin.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.contract.wechat.sgin.dao.ContractDao;
import com.contract.wechat.sgin.entity.ContractEntity;
import com.contract.wechat.sgin.service.ContractService;
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

@Data
@Slf4j
@Service("contractService")
@ConfigurationProperties(prefix = "contract.wechat")
public class ContractServiceImpl extends ServiceImpl<ContractDao, ContractEntity> implements ContractService {
    private String exportfile;

    @Override
    public String exportCurrenPage2xls() {
        ExcelUtil excelUtil = new ExcelUtil();
        List<ContractEntity> list = baseMapper.selectList(new EntityWrapper<ContractEntity>());
        log.warn("查询到userformidlist为 : " + list.toString());
//        List<UserEntity> list = this.selectList(new EntityWrapper<UserEntity>());
        try {
            String filename = "/user" + DateUtil.getFormatDate(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            String filepath = exportfile + filename;
            Map<String, ExportInfo> header = new LinkedHashMap<String, ExportInfo>();
            header.put("contractName", new ExportInfo("合同名称"));
            header.put("content", new ExportInfo("合同内容"));
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
                String contractName = excelUtil.getHSSTextString(row, 0);
                //第一行第二列
                String content = excelUtil.getHSSTextString(row, 1);
                if (!"合同名称".equalsIgnoreCase(contractName)) {
                    return BaseResp.error("表头不对,必须为合同名称");
                }
                if (!"合同内容".equalsIgnoreCase(content)) {
                    return BaseResp.error("表头不对,必须为合同内容");
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
                String contractName = excelUtil.getHSSTextString(row, 0);
                String content = excelUtil.getHSSTextString(row, 1);
                if (StringTools.isNullOrEmpty(contractName)) {
                    return BaseResp.ok("合同名称不能为空");
                }
                if (StringTools.isNullOrEmpty(content)) {
                    return BaseResp.ok("合同内容不能为空");
                }
                //TODO 插入数据库
                ContractEntity contractEntity = new ContractEntity();
                contractEntity.setCreatedAt(LocalDateTime.now());
                boolean result = this.insert(contractEntity);
                if (!result) {
                    faliure++;
                    log.error("第" + i + "行数据插入失败");
                } else {
                    success++;
                }
            }
            return BaseResp.ok("成功插入" + success + "条数据,失败" + faliure + "条数据");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return BaseResp.error();
    }
}
