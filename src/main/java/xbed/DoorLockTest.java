package xbed;


import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.warehouse.data.httpclient.AbstractHttpClient;
import com.warehouse.data.httpclient.HttpClient;
import com.warehouse.data.httpclient.HttpOptions;
import com.warehouse.data.httpclient.HttpParams;
import com.warehouse.data.httpclient.HttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-05-10 18:15
 **/
public class DoorLockTest {

    public final static int    CONNECT_TIMEOUT = 60;
    public final static int    READ_TIMEOUT    = 100;
    public final static int    WRITE_TIMEOUT   = 60;
    private static      int    TIMEOUT         = 29000;
    private static      String CHARSET         = "utf-8";


    public static void main(String[] args) throws ParseException {

        String url = "http://www.cofjs.com:1118";

        String uid = "1010";
        String topic = "bind";
        String lockId = "1001124";
        String gateId = "1000027";
        //绑定锁
        ConfjsProperties bind = ConfjsProperties.newBuilder(uid)
                .topic(topic)
                .payload(new String[]{lockId, gateId}).build();

        //开关锁
        ConfjsProperties lock = ConfjsProperties.newBuilder(uid)
                .topic("open_lock")//"topic":"open_lock", (或:"close_lock","open_lock_only")
                .payload(new String[]{lockId}).build();

        //解绑
        ConfjsProperties unBind = ConfjsProperties.newBuilder(uid)
                .topic("ubind")
                .payload(new String[]{lockId}).build();

        //设置/取消密码
        long timeStamp = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间
        System.out.println(sd);//打印出你要的时间

        ConfjsProperties password = ConfjsProperties.newBuilder(uid)
                .topic("set_lock_pwd")
                .payload(new String[]{lockId, String.valueOf(timeStamp)}).build();

        System.out.println(JSON.toJSONString(password));
        call(url, password);
/*
        HttpClient client = HttpClient.getInstance();
        HttpParams httpParams = new HttpParams();
        httpParams.put("topic", password.getTopic());
        httpParams.put("payload", password.getPayload());
        HttpOptions options = new HttpOptions.Builder().build();
        HttpResponse response = new HttpResponse();
        client.request(AbstractHttpClient.METHOD.POST, url, httpParams, options, response);

        System.out.println("========> " + response.getResult());*/
        /*Map<String, String> params = Maps.newHashMap();
        params.put("topic", password.getTopic());
        params.put("payload", password.getPayload());
        String result = postData(url, params, null);
        System.out.println("====xx====> " + result);*/

       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeStamp = System.currentTimeMillis();
        System.out.println(timeStamp);

        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        System.out.println(sd);

        Date date = sdf.parse(sd);
        long ts = date.getTime();
        System.out.println(ts);

        System.out.println(sdf.format(new Date(Long.parseLong(String.valueOf(ts)))));*/

        String s = "101005196|1001124|630819";
        int index = s.lastIndexOf("|") + 1;
        int i = s.indexOf("|");
        System.out.println(s.substring(index));
        System.out.println(s.substring(0, i));



        String c = "cofjs_2";
        System.out.println("-------------->>>>>>>>" + c.indexOf("co1fjs"));
    }


    public static void call(String url, ConfjsProperties properties) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool())   // - 开启链接池
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();//设置连接超时时间

        RequestBody requestBody = new FormBody.Builder()
                .add("topic", properties.getTopic())
                .add("payload", properties.getPayload()).build();
        try {
            Response response = okHttpClient.newCall(new Request.Builder()
                    .post(requestBody)
                    .url(url)
                    .build()).execute();


            if (response != null) {
                System.out.println("-----------------> " + response.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public enum CallType {

        BIND, UNBIND, PASSWORD, LOCK

    }


    public static final String postData(String url, Map<String, String> params, Map<String, String> headers) {
        Date beginDate = new Date();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = null;
        try {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT).build();
            post.setConfig(requestConfig);

            if (params != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<Map.Entry<String, String>> sets = params.entrySet();
                for (Map.Entry<String, String> map : sets) {
                    nvps.add(new BasicNameValuePair(map.getKey(), map.getValue()));
                }
                post.setEntity(new UrlEncodedFormEntity(nvps, CHARSET));
            }
            if (headers != null) {
                Set<Map.Entry<String, String>> sets = headers.entrySet();
                for (Map.Entry<String, String> map : sets) {
                    post.setHeader(map.getKey(), map.getValue());
                }
            }
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, CHARSET);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("POST请求。请求链接：%s，请求参数：%s。返回信息：%s。响应时间：%s毫秒", url, (null != params ? params.toString() : ""), result, (new Date().getTime() - beginDate.getTime())));
            return result;
        }
    }
}
