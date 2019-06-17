package com.warehouse.data;

import com.warehouse.data.httpclient.HttpClient;
import com.warehouse.data.httpclient.HttpOptions;
import com.warehouse.data.httpclient.HttpResponse;

import gui.ava.html.image.generator.HtmlImageGenerator;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2019-05-25 15:39
 **/
public class Html2ImageTest {


    public static void main(String[] args) throws InterruptedException {
        String url = "https://blog.csdn.net/xu_san_duo/article/details/78084582";
        HtmlImageGenerator imageGenerator = new HtmlImageGenerator();

        HttpResponse httpResponse = new HttpResponse();

        HttpOptions.Builder builder = new HttpOptions.Builder();

        HttpClient.getInstance().request(HttpClient.METHOD.GET, url, builder.build(), httpResponse);

        String result = httpResponse.getResult();
        System.out.println(result);

        imageGenerator.loadUrl(url);
        Thread.sleep(3000);

        //imageGenerator.loadHtml(result);
        imageGenerator.getBufferedImage();
        imageGenerator.saveAsImage("/Users/strawhat925/hello-world.png");
        imageGenerator.saveAsHtmlWithMap("hello-world.html", "hello-world.png");

    }

/*

    public static void main(String[] args) {
        String url = "http://192.168.1.138:8080/#/user/invite?inviteCode=123456&company=hc";

        HttpResponse httpResponse = new HttpResponse();

        HttpOptions.Builder builder = new HttpOptions.Builder();

        HttpClient.getInstance().request(HttpClient.METHOD.GET, url, builder.build(), httpResponse);

        String result = httpResponse.getResult();
        System.out.println(result);

        Html2Image html2Image = Html2Image.fromHtml(result);

        html2Image.

        *//*HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        imageGenerator.loadHtml(result);
        imageGenerator.getBufferedImage();
        imageGenerator.saveAsImage("/Users/strawhat925/hello-world.png");
        imageGenerator.saveAsHtmlWithMap("hello-world.html", "hello-world.png");*//*

    }*/
}
