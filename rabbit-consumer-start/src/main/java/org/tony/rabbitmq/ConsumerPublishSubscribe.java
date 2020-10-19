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
public class ConsumerPublishSubscribe {

    private static final String EXCHANGE_NAME = "exchange-test";

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            //生成一个随机的队列
            String queueName = channel.queueDeclare().getQueue();
            //将队列与exchange进行绑定
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received " + message);
            };
            channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
