package com.contract.wechat.sgin.utils;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImageToPDF {
    public static void main(String[] args) {

//创建PDF文档
        PdfDocument doc = new PdfDocument();

        //添加一个页面
        PdfPageBase page = doc.getPages().add();

        //加载图片
        PdfImage image = PdfImage.fromFile("C:\\Users\\leo\\Desktop"+File.separator+"python.jpg");

//绘制图片到PDF并设置其在PDF文件中的位置和大小
        double widthFitRate = getImgWidth(new File("C:\\Users\\leo\\Desktop"+File.separator+"python.jpg")) / page.getActualBounds(true).getWidth();
        double heightFitRate = getImgHeight(new File("C:\\Users\\leo\\Desktop"+File.separator+"python.jpg")) / page.getActualBounds(true).getHeight();

        double fitRate = Math.max(widthFitRate, heightFitRate);
        double fitWidth = getImgWidth(new File("C:\\Users\\leo\\Desktop"+File.separator+"python.jpg")) / fitRate * 0.8f;
        double fitHeight = getImgHeight(new File("C:\\Users\\leo\\Desktop"+File.separator+"python.jpg")) / fitRate * 0.8f;

        page.getCanvas().drawImage(image, 50, 30, fitWidth, fitHeight);

        //保存并关闭
        doc.saveToFile("C:\\Users\\leo\\Desktop"+File.separator+"python.pdf");
        doc.close();
    }

    public static int getImgWidth(File ImageFile) {

        InputStream is = null;
        BufferedImage src = null;

        int ret = -1;

        try {
            is = new FileInputStream(ImageFile);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getWidth(null);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     *      * @param ImageFile
     *      * @return image height
     *     
     */

    public static int getImgHeight(File ImageFile) {

        InputStream is = null;
        BufferedImage src = null;

        int ret = -1;

        try {
            is = new FileInputStream(ImageFile);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getHeight(null);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
