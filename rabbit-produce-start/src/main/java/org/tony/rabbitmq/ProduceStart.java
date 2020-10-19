package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * produce 发送rabbit消息
 * 队列是rabbitmq内部邮箱的名称。消息只能存储在队列中
 * 队列受主机内存和磁盘的限制，它本质上是一个大的消息缓存区
 * 许多生产者可以发送一个队列的消息，多个消费者可以尝试从一个队列接受数据
 * @ProjectName kafka-example
 * @PackageName org.tony
 */
public class ProduceStart {

    public static final String QUEUE_NAME = "hello";

    public static void main(String... args) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        try {
            //创建一个rabbitmq的连接
            Connection connection = connectionFactory.newConnection();
            //创建一个管道进行交互，channel 完成工作的大多数API所在的位置
            //注意，我们可以使用try-with-resources语句，因为Connection和Channel都实现java.io.Closeable
            // 这样，我们无需在代码中显式关闭它们
            Channel channel = connection.createChannel();
            //声明一个队列是幂等的，只有当它不存在时才会创建它
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello world!";
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println(" [x] Send '" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
