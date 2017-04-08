package com.warehouse.data.qiniu;

import com.qiniu.util.Auth;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.qiniu
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-07 17:35
 **/
public class QiniuAuth {

    public static final String accessKey = "25mIcQbTECQqHgqnuqcsl_U3dfx1nH-j50u5lg0v";
    public static final String secretKey = "RX0P40iL7QQHygcCyGDNC1G_xc58nfq_MBWqN5eT";
    private static final String bucket = "warehouse";


    public static String auth(){
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }


    public static void main(String[] args) {
        String token = QiniuAuth.auth();
        System.out.println(token);
    }
}
