package org.tony.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private static final String REQUEST_QUEUE_NAME = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");

        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String... args) {
        try {
            RPCClient rpcProduce = new RPCClient();
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = rpcProduce.call(i_str);
                System.out.println(" [.] Got " + response);
            }

        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();
        //队列名称
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties properties = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("",REQUEST_QUEUE_NAME,properties,message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName,true,(consumerTag,delivery) -> {
            if(delivery.getProperties().getCorrelationId().equals(corrId)){
                response.offer(new String(delivery.getBody(),"UTF-8"));
            }
        },consumerTag -> {});

        String result = response.take();
        channel.basicCancel(ctag);
        return  result;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
