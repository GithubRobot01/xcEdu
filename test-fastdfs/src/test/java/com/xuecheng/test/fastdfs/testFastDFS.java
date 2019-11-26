package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testFastDFS {
    //上传测试
    @Test
    public void uploadTest(){
        //加载配置文件
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient,用于请求TrackerServer
            TrackerClient trackerClient=new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            //向storage服务器上传文件
            String filePath="D:/panda.jpg";
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //下载测试
    @Test
    public void downloadTest(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer=trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            //下载文件
            String fileId="group1/M00/00/01/wKgZmV3bm0aAXb1nAAlD11okc6g216.jpg";
            byte[] bytes = storageClient1.download_file1(fileId);
            //使用输出流
            FileOutputStream fileOutputStream=new FileOutputStream(new File("d:/panda1.jpg"));
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
