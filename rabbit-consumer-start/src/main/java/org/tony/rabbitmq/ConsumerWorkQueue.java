package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ConsumerWorkQueue {

    private static final String QUEUE_NAME = "work-queue";

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //消息的持久化 channel.queueDeclare(QUEUE_NAME, true, false, false, null); 第二个参数标记持久化
            //当我们想队列中发送消息的时候，等待消费者消费的时候，rabbitmq宕机了或者关闭了。队列中的消息将会丢失，为了防止消息丢失
            //可以使用将消息进行持久化操作
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received " + message);
                try {
                    doWork(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] DONE");
                    //当消息接受完成的时候，将其关闭
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            boolean autoAck = true;
            //消息确认 channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {}); autoAck参数
            //作用：rabbitmq一旦向消费者传递了一条消息，便立即将其标记为删除。如果我们在发送消息处理的时候出现错误，我们将丢失处理的消息
            //消费者发送回接受消息确认，以告知rabbitmq已经接受，处理了特定的消息，并且rabbitmq可以只有的删除消息
            //消费者死亡时，rabbitmq会重新传递消息
            channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static void doWork(String message) throws InterruptedException {
        for (char ch : message.toCharArray()) {
            if (ch == '.') {
                Thread.sleep(1_000);
            }
        }
    }

}
