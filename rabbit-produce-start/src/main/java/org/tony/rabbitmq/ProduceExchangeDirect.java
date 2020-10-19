package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Date 2020/10/14
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ProduceExchangeDirect {

    private static final String EXCHANE_NAME = "exchange-direct";

    public static void main(String ... args){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //direct 可以将消息根据不同的关键字推送到不同的队列中
            //但是direct可以根据不同的关键字路由，但是不能基于多个条件进行路由
            channel.exchangeDeclare(EXCHANE_NAME,"direct");
            String severity = getServerity(args);
            String message = getMessage(args);
            //severity 参数就是标记相关的队列
            //channel.basicPublish(EXCHANE_NAME,severity,null,message.getBytes("UTF-8"));
            channel.basicPublish(EXCHANE_NAME,severity,null,message.getBytes("UTF-8"));
            System.out.println(" [x] Send " + severity + ":" + message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static String getMessage(String[] args) {
        if(args.length < 2){
            return "Hello world!";
        }
        return JoinStringUtils.joinString(args," ",1);
    }

    private static String getServerity(String[] args) {
        if(args.length < 1){
            return "info";
        }
        return args[0];
    }

}
