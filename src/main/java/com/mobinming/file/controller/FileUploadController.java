package com.mobinming.file.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class FileUploadController {

    /*
     * 获取file.html页面
     */
    @RequestMapping("file")
    public String file() {
        return "/file";
    }

    /**
     * 实现文件上传
     */
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("fileName") MultipartFile file) {
        if (file.isEmpty()) {
            return "false";
        }
        String fileName = file.getOriginalFilename();
        int size = (int) file.getSize();
        System.out.println(fileName + "-->" + size);

        String path = "C:/test";
        File dest = new File(path + "/" + fileName);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest); //保存文件
            return "true";
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        }
    }


    @RequestMapping("multifile")
    public String multifile() {
        return "/multifile";
    }

    /**
     * 实现多文件上传
     */
    /*public @ResponseBody String multifileUpload(@RequestParam("fileName")List<MultipartFile> files) */
    public @ResponseBody
    String multifileUpload(HttpServletRequest request) {

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("fileName");

        if (files.isEmpty()) {
            return "false";
        }

        String path = "C:/test";

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if (file.isEmpty()) {
                return "false";
            } else {
                File dest = new File(path + "/" + fileName);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "false";
                }
            }
        }
        return "true";
    }

    /*
     *实现文件下载
     */
    @RequestMapping("/fileDownload")
    public String fileDownload(Model model) {

        String inputPath = "C:/test";
        File file = new File(inputPath);      //获取其file对象
        File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中
        //String[] files=new String[100];
        ArrayList files = new ArrayList();
        for (File f : fs) {                //遍历File[]数组
            String fileName = f.getName();  //获取文件和目录名
            if (!f.isDirectory()) {  //另外可用fileName.endsWith("txt")来过滤出以txt结尾的文件
                //logger.info("isFile:" + f);     //打印文件全路径
                //logger.info("isFile:" + fileName); //打印文件名
                files.add(fileName);
            }

        }
        model.addAttribute("inputPath", inputPath);
        model.addAttribute("fileNames", files);
        return "fileDownload";
    }


    @GetMapping("/download/{downFileName}")
    public String downLoad(@PathVariable String downFileName,
                           HttpServletResponse response)
            throws UnsupportedEncodingException {
        //String filename="test.jpg";
        String filename = downFileName;
        String filePath = "C:/test";
        File file = new File(filePath + "/" + filename);
        if (file.exists()) { //判断文件父目录是否存在
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}

