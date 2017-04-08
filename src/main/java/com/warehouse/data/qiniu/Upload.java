package com.warehouse.data.qiniu;

import com.google.gson.Gson;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.qiniu
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-07 17:38
 **/
public class Upload {

    public void upload() {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());

        UploadManager uploadManager = new UploadManager(cfg);


        String filePath = "D:\\用户目录\\我的文档\\WeChat Files\\strawhat925\\Image\\Image\\c3e80174980acc676b0ecbe8bd35f725.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;

        String upToken = QiniuAuth.auth();

        try {
            Response response = uploadManager.put(filePath, key, upToken);
            DefaultPutRet defaultPutRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(defaultPutRet.key);
            System.out.println(defaultPutRet.hash);
        } catch (QiniuException e) {
            e.printStackTrace();
            Response response = e.response;
            System.out.println(response.toString());
        }
    }


    public static void main(String[] args) {
        Upload upload = new Upload();
        upload.upload();
    }
}
