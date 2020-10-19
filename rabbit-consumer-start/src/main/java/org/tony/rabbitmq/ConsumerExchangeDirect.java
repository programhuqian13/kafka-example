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
public class ConsumerExchangeDirect {

    private static final String EXCHANGE_NAME = "exchange-direct";

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            //获取随机的队列名
            String queueName = channel.queueDeclare().getQueue();

            if (args.length < 1) {
                System.out.println("Usage: ReceiveMessageDirect [info] [warning] [error]");
                System.exit(1);
            }

            for (String serverity : args) {
                channel.queueBind(queueName, EXCHANGE_NAME, serverity);
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received " + delivery.getEnvelope().getRoutingKey() + ":" + message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
