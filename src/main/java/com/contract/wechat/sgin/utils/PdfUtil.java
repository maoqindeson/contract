package com.contract.wechat.sgin.utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import com.lowagie.text.pdf.PdfReader;
public class PdfUtil {
//    public static void main(String[] args){
////        String filePath = "D://Test/xxx.pdf";
//        String filePath = "C://Users//leo//Pictures/050820361390ImagePDF.pdf";
//
//        List<String> imageList = pdfToImagePath(filePath,file);
//        Iterator<String> iterator = imageList.iterator();
//        while(iterator.hasNext()){
//
//            System.out.println(iterator.next());
//        }
////        pdfToImage(filePath);
//    }
    public static List<String> pdfToImagePath(String filePath,File file){
        List<String> list = new ArrayList<>();
        String fileDirectory = filePath.substring(0,filePath.lastIndexOf("."));//获取去除后缀的文件路径

        String imagePath;
//        File file = new File(filePath);
        try {
            File f = new File(fileDirectory);
            if(!f.exists()){
                f.mkdir();
            }
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for(int i=0; i<pageCount; i++){
                // 方式1,第二个参数是设置缩放比(即像素)
                // BufferedImage image = renderer.renderImageWithDPI(i, 296);
                // 方式2,第二个参数是设置缩放比(即像素)
                BufferedImage image = renderer.renderImage(i, 1.25f);  //第二个参数越大生成图片分辨率越高，转换时间也就越长
                imagePath = fileDirectory + File.separator+i + ".jpg";
                ImageIO.write(image, "PNG", new File(imagePath));
                list.add(imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
