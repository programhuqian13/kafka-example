package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Consumer 接受队列中的消息
 * 生产者、消费者和代理不必驻留在同一主机上;实际上，在大多数应用程序中，它们不会。应用程序可以同时是生产者和消费者
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ConsumerStart {

    public static final String QUEUE_NAME = "hello";

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //声明队列，因为我们可能在启动发布者之前启动消费者，所以为了确保在尝试使用队列中的消息之前确保该队列存在
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //使用DeliverCallback接口来缓冲服务器推送给我们的消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"utf-8");
                System.out.println(" [x] Received " + message);
            };
            channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }


}
