package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布/订阅模式：消息传达给多个消费者
 * Exchanges：生产者只能向Exchanges发送消息
 * Exchanges是一件非常简单的事情。
 * 一方面，它接收来自生产者的消息，
 * 另一方面，将它们推入队列。 Exchanges必须确切知道如何处理收到的消息
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ProducePublishSubscribe {

    private static final String EXCHANGE_NAME = "exchange-test";

    public static void main(String ... args){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //声明exchange并设定exchange的类型 exchange的类型有四种：1、direct 2.topic 3.headers 4.fanout
            channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
            String message = args.length < 1 ? "info: Hello world!" : String.join("",args);
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes("UTF-8"));
            System.out.println(" [x] Send " + message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
