package com.contract.wechat.sgin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.contract.wechat.sgin.aop.WebRecord;
import com.contract.wechat.sgin.entity.ContractEntity;
import com.contract.wechat.sgin.entity.ContractFileEntity;
import com.contract.wechat.sgin.service.*;
import com.contract.wechat.sgin.utils.BaseResp;
import com.contract.wechat.sgin.utils.PdfUtil;
import com.contract.wechat.sgin.utils.wechat.StringTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.FileImageInputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Data
@Controller
@RequestMapping("/wechat/file")
@ConfigurationProperties(prefix = "contract.wechat")
public class FileController {
    private String importfile;
    private String contractFilePath;
    @Autowired
    private UserService userService;
    @Autowired
    private ContractFileService contractFileService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private EmpolyeService empolyeService;
    @Autowired
    private CompanyService companyService;

    /**
     * 导出数据库员工信息和所属公司信息
     */
    @RequestMapping(value = "/exportEmpolyeAll", produces = "text/plain")
    public String exportEmpolyeAll(HttpServletResponse response) {
        String filename = empolyeService.exportCurrenPage2xls();
        if (StringTools.isNullOrEmpty(filename)) {
            log.error("导出失败,filename为: " + filename);
            return null;
        }
        File file = new File(filename);
        if (!file.exists()) {
            log.warn("文件" + filename + "不存在");
            return "文件" + filename + "不存在";
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + "employeformidExcel");// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            log.warn("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("下载异常");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }




    /**
     * 上传PDF转图片到服务器,并插入到合同文件表,返回合同文件Id的list
     */
    @WebRecord
    @PostMapping("/uploadPdfFile")
    @ResponseBody
    public BaseResp uploadPdfFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (null == file || file.isEmpty()) {
            return BaseResp.error("请选择上传文件");
        }
        try {
        String saveFileName = file.getOriginalFilename();
        String filePath = contractFilePath;
        File pdfFile = new File(contractFilePath,saveFileName);
        List<Integer>fileIds = new ArrayList<>();
        FileUtils.copyInputStreamToFile(file.getInputStream(), pdfFile);

            List<String> list = PdfUtil.pdfToImagePath(filePath +File.separator+ saveFileName, pdfFile);
            //拿到pdf转成jpg的路径
            for (int i = 0; i < list.size(); i++) {
                String imagePath = list.get(i);
                String fileDirectory = imagePath.substring(0, imagePath.lastIndexOf(File.separator));
                ContractFileEntity contractFileEntity = new ContractFileEntity();
                contractFileEntity.setName(String.valueOf(i));
                contractFileEntity.setPath(fileDirectory);
                contractFileEntity.setCreatedAt(LocalDateTime.now());
                boolean b = contractFileService.insert(contractFileEntity);
                if (!b) {
                    log.error("插入合同文件失败");
                    return BaseResp.error("插入合同文件失败");
                }
                fileIds.add(contractFileEntity.getId());
            }
            return BaseResp.ok("上传成功"+fileIds);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件异常");
        }
        return BaseResp.error("上传失败");

    }


    /**
     * 导出数据库合同的表格
     */
    @RequestMapping(value = "/exportContract", produces = "text/plain")
    public String exportContract(HttpServletResponse response) {
        String filename = this.contractService.exportCurrenPage2xls();
        if (StringTools.isNullOrEmpty(filename)) {
            log.error("导出失败,filename为: " + filename);
            return null;
        }
        File file = new File(filename);
        if (!file.exists()) {
            log.warn("文件" + filename + "不存在");
            return "文件" + filename + "不存在";
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + "userformidExcel");// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            log.warn("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从服务器下载文件
     */
    @WebRecord
    @RequestMapping("/downloadFile")
    public String downloadFile(HttpServletResponse response, Integer fileId) {
        if (fileId == null || fileId == 0) {
            log.warn("请输入文件ID");
            return "请输入文件ID";
        }
        ContractFileEntity contractFileEntity = contractFileService.selectOne(new EntityWrapper<ContractFileEntity>().eq("id", fileId));
        if (contractFileEntity == null) {
            log.error("文件名不存在");
            return "文件名不存在";
        }
        String fileName = contractFileEntity.getName();
        String filePath = contractFileEntity.getPath();
        File file = new File(filePath, fileName + ".jpg");
        if (!file.exists()) {
            log.warn("文件" + fileName + "不存在");
            return "文件" + fileName + "不存在";
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=fileName.jpg");// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            log.warn("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @WebRecord
    @RequestMapping("/downloadPicture")
    public String downloadPicture(HttpServletResponse response, Integer fileId) {
        if (fileId == null || fileId == 0) {
            log.warn("请输入文件ID");
            return "请输入文件ID";
        }
        ContractFileEntity contractFileEntity = contractFileService.selectOne(new EntityWrapper<ContractFileEntity>().eq("id", fileId));
        if (contractFileEntity == null) {
            log.error("文件不存在");
            return "文件不存在";
        }
        String filename = contractFileEntity.getName();
        String filePath = contractFileEntity.getPath();
        File file = new File(filePath, filename + ".jpg");
        if (!file.exists()) {
            log.warn("文件" + filename + "不存在");
            return "文件" + filename + "不存在";
        }
        try {
            FileImageInputStream fis = new FileImageInputStream(file);
            int streamLength = (int) fis.length();
            byte[] image = new byte[streamLength];
            fis.read(image, 0, streamLength);
            fis.close();
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.setHeader("Content-Disposition", "attachment;filename=filename.jpg");// 设置文件名
            OutputStream os = response.getOutputStream();
            os.write(image);
            os.flush();
            os.close();
            log.warn("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导入员工表格并实现了所属公司的关联到数据库
     */
    @PostMapping("/importEmpolye")
    @ResponseBody
    public BaseResp importEmpolye(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return BaseResp.error("请上传文件");
        }
        String strDir = importfile;
//        log.warn("");
        String fileName = file.getOriginalFilename();
        if (!fileName.contains("xlsx")) {
            return BaseResp.error("请重新上传,文件必须为.xlsx拓展名格式");
        }
        File dir = new File(strDir);
        String fullName = strDir + File.separator + fileName;
        try {
            if (dir.exists() || dir.mkdirs()) {
            }
            FileOutputStream os = new FileOutputStream(fullName);
            InputStream in = file.getInputStream();
            int b = 0;
            while ((b = in.read()) != -1) {
                os.write(b);
            }
            os.flush();
            os.close();
            in.close();
            BaseResp baseResp = empolyeService.importEmpolye(fullName);
            return baseResp;
        } catch (Exception e) {
            log.error("error: " + e);
            e.printStackTrace();
        }
        return BaseResp.error();
    }


    /**
     * 导入合同表格到数据库
     */
    @PostMapping("/importContract")
    @ResponseBody
    public BaseResp importContract(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return BaseResp.error("请上传文件");
        }
        String strDir = importfile;
        String fileName = file.getOriginalFilename();
        if (!fileName.contains("xlsx")) {
            return BaseResp.error("请重新上传,文件必须为.xlsx拓展名格式");
        }
        File dir = new File(strDir);
        String fullName = strDir + "/" + fileName;
        try {

            if (dir.exists() || dir.mkdirs()) {
            }
            FileOutputStream os = new FileOutputStream(fullName);
            InputStream in = file.getInputStream();
            int b = 0;
            while ((b = in.read()) != -1) {
                os.write(b);
            }
            os.flush();
            os.close();
            in.close();
            BaseResp baseResp = contractService.importUsers(fullName);
            return baseResp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
