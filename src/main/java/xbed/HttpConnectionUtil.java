package xbed;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-06-25 17:41
 **/

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 * Java原生的API可用于发送HTTP请求，即java.net.URL、java.net.URLConnection，这些API很好用、很常用，
 * 但不够简便；
 *
 * 1.通过统一资源定位器（java.net.URL）获取连接器（java.net.URLConnection） 2.设置请求的参数 3.发送请求
 * 4.以输入流的形式获取返回内容 5.关闭输入流
 *
 * @author H__D
 */
public class HttpConnectionUtil {


    /**
     * @param urlPath     下载路径
     * @param downloadDir 下载存放目录
     * @return 返回下载文件
     */
    public static void downloadFile(String url, String downloadDir) {
        File file = null;
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 发送请求参数
            // flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            // 必须设置false，否则会自动redirect到Location的地址
            conn.setInstanceFollowRedirects(false);

            String location = conn.getHeaderField("Location");
            realUrl  = new URL(location);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            in = new BufferedInputStream(conn.getInputStream());

            System.out.println(conn.getResponseCode());

            file = new File(downloadDir + "merchant.apk");
            OutputStream out = new FileOutputStream(file);

            int fileLength = conn.getContentLength();
            System.out.println("file length---->" + fileLength);

            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = in.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
                // 打印下载百分比
                 System.out.println("下载了-------> " + len * 100 / fileLength + "%\n");
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        // 下载文件测试
        downloadFile("https://www.pgyer.com/apiv2/app/install?appKey=59fc2dcd482f7bfe0493cc52fbecbe7f&_api_key=1aa614f491473989558d1a75080b1cec", "/Users/strawhat925/Desktop/");

    }

}
