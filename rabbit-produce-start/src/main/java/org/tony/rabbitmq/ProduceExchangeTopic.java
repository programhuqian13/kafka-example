package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Topic exchange
 * Topic exchange功能强大，可以像其他交流一样进行
 * 发送到Topic exchange的消息不能具有任意的routing_key-它必须是单词列表，以点分隔。
 * 这些词可以是任何东西，但通常它们指定与消息相关的某些功能。
 * 一些有效的路由关键示例：“ stock.usd.nyse”，“ nyse.vmw”，“ quick.orange.rabbit”。
 * 路由关键字中可以包含任意多个单词，最多255个字节
 * 绑定密钥也必须采用相同的形式。
 * 主题交换背后的逻辑类似于直接交换的逻辑-使用特定路由密钥发送的消息将被传递到所有使用匹配绑定密钥绑定的队列
 * * 可以代替一个单词。
 * # 可以替代零个或多个单词。
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ProduceExchangeTopic {

    private static final String EXCHANGE_NAME = "exchange-topic";

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,"topic");

            //获取路由
            String routingKey = getRouting(args);
            String message = getMessage(args);

            channel.basicPublish(EXCHANGE_NAME,routingKey,null,message.getBytes("UTF-8"));
            System.out.println(" [x] Send " + routingKey + " : " + message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static String getMessage(String[] args) {
        if(args.length < 2)
            return "Hello world!";
        return JoinStringUtils.joinString(args," ",1);
    }


    private static String getRouting(String[] args) {
        if(args.length < 1)
            return "anonymous.info";
        return args[0];
    }

}
