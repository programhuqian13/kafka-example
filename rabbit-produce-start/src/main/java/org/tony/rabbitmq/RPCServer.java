package org.tony.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class RPCServer {

    private static final String REQUEST_QUEUE_NAME = "rpc_queue";

    private static int fib(int i) {
        if (i == 0) return 0;
        if (i == 1) return 1;
        return fib(i - 1) + fib(i - 2);
    }

    public static void main(String... args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(REQUEST_QUEUE_NAME);
            channel.basicQos(1);  //消息确认
            System.out.println(" [x] Awaiting RPC requests");
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);
                    System.out.println("[.]fib(" + message + ")");
                    response += fib(n);
                } catch (RuntimeException e) {
                    System.out.println(e.getLocalizedMessage());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    //rabbitmq消费者线程通知rabbitmq 生产者自己的线程
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(REQUEST_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
            }));

            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
