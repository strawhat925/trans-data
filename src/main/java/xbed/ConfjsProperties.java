package xbed;

import org.apache.commons.codec.binary.Base64;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-05-11 10:28
 **/
public final class ConfjsProperties {

    private final static String secret = "nkbn3zcry";

    private String topic;
    private String payload;

    public ConfjsProperties(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }


    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }


    public static Builder newBuilder(String uid) {
        return new Builder(uid);
    }

    public static class Builder {
        private final String uid;
        private       String topic;
        private       String payload;

        public Builder(String uid) {
            this.uid = uid;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder payload(String... params) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String activeKey = hmac_sha256(secret, timestamp);
            StringBuffer sb = new StringBuffer();
            sb.append(uid)
                    .append(random(5, 10))
                    .append("|")
                    .append(activeKey)
                    .append("|");

            for (String str : params) {
                sb.append(str).append("|");
            }
            this.payload = sb.substring(0, sb.length() - 1);
            return this;
        }


        public final ConfjsProperties build() {
            return new ConfjsProperties(topic, payload);
        }


        private String random(int count, int bound) {
            StringBuffer sb = new StringBuffer();
            Random random = new Random();
            for (int i = 0; i < count; i++) {
                sb.append(random.nextInt(bound));
            }

            return sb.toString();
        }


        private String hmac_sha256(String secret, String timestamp) {
            try {
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
                sha256_HMAC.init(secret_key);

                String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(timestamp.getBytes()));
                hash = hash.substring(0, 7);
                return hash + timestamp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
