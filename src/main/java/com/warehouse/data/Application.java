package com.warehouse.data;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import com.warehouse.data.trans.ExportSqlFileThread;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import io.netty.buffer.ByteBuf;

/**
 * Hello world!
 */
public class Application {
    final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static void main(String[] args) {
        //ExportSqlFileThread exportThread = new ExportSqlFileThread(5);
        //exportThread.execute();

        System.out.println(0x1 << 3);


        //3
        //2
        //1

        //5
/*

        System.out.println((int)(5/3) - (4 /3 ));

        System.out.println(0 - (0 % (1024 * 1024 * 1024)));

        String s = offset2FileName(0L);
        System.out.println(s);

        String x = offset2FileName(1024 * 1024 * 1024);
        System.out.println(x);

        ByteBuffer bb = ByteBuffer.allocate(8);
        socketAddress2ByteBuffer(new InetSocketAddress(8761), bb);

        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.flip();
        byteBuffer.limit(16);

        byteBuffer.put(bb);
        byteBuffer.putLong(1024 * 1024 * 1024);

        String t = bytes2string(byteBuffer.array());
        System.out.println(t + "--->" + t.length());
*/

        /*Date beginDate = new Date();
        long beginTime = Date.UTC(beginDate.getYear(), beginDate.getMonth(), beginDate.getDate(), beginDate.getHours(), beginDate.getMinutes(), beginDate.getSeconds());


        //1541778226000
        //1452754256
        System.out.println(beginTime);

        System.out.println("ae21d8219a768bcb9eb897370062dd31".length());


        String id = "U8947W19892471721984\\u0000";
        System.out.println(id.indexOf("\\"));
        System.out.println("-------<>" + id.substring(0, id.indexOf("\\u")));


        BiMap<String,String> LQ_COMPANY_MAPPING = HashBiMap.create();
        LQ_COMPANY_MAPPING.put("9002","9ed69e4074cb46cb97dd62ebdca2a408"); //中铁测试

        System.out.println("===========>" + LQ_COMPANY_MAPPING.values());


        String reg = "^((?!NIM).)*$";
        System.out.println("NIMgdfgfgdffgn".matches(reg));

        Map<String, Object> map = Maps.newHashMap();
        System.out.println(map.get("ttt"));*/

        //packageMessage("", "111", 22.22);

        System.out.println((0x1 << 1) | (0x1 << 2));

        System.out.println((short)1);
    }


    private static String packageMessage(String logisticCompanyId, String trackingNumber, double rebate) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("mch_appid", "1");
        params.put("mchid", "2");
        params.put("nonce_str", UUID.randomUUID().toString());
        params.put("partner_trade_no", trackingNumber);
        params.put("check_name", "FORCE_CHECK");
        params.put("amount", rebate * 100);
        params.put("desc", "拉包服务费");
        params.put("spbill_create_ip", "192.168.1.131");

        StringBuffer sb = new StringBuffer();
        params.keySet().forEach(key -> {
            sb.append(key).append("=").append(params.get(key)).append("&");
        });

        String stringA = sb.substring(0, sb.length() - 2).toString();
        String stringSignTemp = stringA + "&key=";
        String sign = "";

        params.put("sign", sign);

        //直接转xml
        sb.delete(0, sb.length());

        sb.append("<xml>");
        params.keySet().forEach(key -> {
            sb.append("<").append(key).append(">").append(params.get(key)).append("</").append(key).append(">");
        });
        sb.append("</xml>");
        System.out.println(sb.toString());
        return "";
    }


    public static String offset2FileName(final long offset) {
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(20);
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        return nf.format(offset);
    }

    public static String bytes2string(byte[] src) {
        char[] hexChars = new char[src.length * 2];
        for (int j = 0; j < src.length; j++) {
            int v = src[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static ByteBuffer socketAddress2ByteBuffer(final SocketAddress socketAddress, final ByteBuffer byteBuffer) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        byteBuffer.put(inetSocketAddress.getAddress().getAddress(), 0, 4);
        byteBuffer.putInt(inetSocketAddress.getPort());
        byteBuffer.flip();
        return byteBuffer;
    }
}
