package com.mmall.service.impl;

import com.mmall.service.IFileService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Auther: hexin
 * @Date: 2018/4/24 16:54
 * @Description:   文件上传服务
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 文件上传
     * @param file 文件
     * @param path 路径
     * @return
     */
    private String upload(MultipartFile file,String path){
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //上传文件的名字
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件名:{},上传的路径为:{},新文件名:{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()){
            //允许创建
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件上传成功了
            //todo 将文件上传到fttp服务器上
            //todo  上传之后。删除upload文件
        }catch (IOException e){
            logger.error("上传文件异常",e);
        }
        return targetFile.getName();
    }
}
