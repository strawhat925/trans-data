package rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-10-20 11:23
 **/
public class SimpleExample_SyncProducer {


    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        /*//Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        //Specify name server address.
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();

        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body

            Message msg = new Message("TopicTest" *//*Topic*//*, "TagA" *//*Tag*//*, ("Hello RocketMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET) *//*Message body*//*);

            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }

        //Shut down once the producer install is not longer in use.*/

        System.out.println(1 << 0);

        System.out.println(1 | 0);
    }

}
