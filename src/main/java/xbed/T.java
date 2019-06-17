package xbed;

import org.apache.commons.codec.binary.Base64;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-05-11 17:21
 **/
public class T {
    private final static String secret = "nkbn3zcry";

    public static void main(String[] args) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = hmac_sha256(secret, timestamp);

        System.out.println(hash);

        String h = hash.substring(0, 7);
        String t = hash.substring(7);
        System.out.format("哈希值为：%s\r\n", h);
        System.out.format("时间戳为：%s\r\n", t);


        String ah = hmac_sha256(secret, t).substring(0, 7);
        System.out.format("拿到参数重新hash的前七位为：%s", ah);

        assert ah.equals(h);
    }


    @SuppressWarnings("Duplicates")
    private static String hmac_sha256(String secret, String timestamp) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(timestamp.getBytes()));

            System.out.println("=======> " + hash);

            hash = hash.substring(0, 7);
            return hash + timestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
