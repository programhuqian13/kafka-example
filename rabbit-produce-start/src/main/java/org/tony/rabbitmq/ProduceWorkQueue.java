package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作队列：主要思想是避免立即执行资源密集型任务，而不得不等待它完成。相反我们安排任务在以后完成
 * 我们将任务封装为消息并将其发送到队列。在后台运行的工作进程将弹出任务并最终执行作业。当运行多个work时，任务将在他们之间共享
 * 工作队列的优势之一是能够轻松并行化工作。如果我们正在积压工作，我们可以添加更多的消费者
 * 默认情况下，rabbitmq将按顺序将每个消息给下一个消费者。平均而言，每个消费者都会收到相同数量的消息，这种分发的方式称为循环
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class ProduceWorkQueue {

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
            channel.basicQos(1);  //告诉RabbitMQ一次不要给工人一个以上的消息。换句话说，在处理并确认上一条消息之前，不要将新消息发送给消费者。而是将其分派给不忙的下一个消费者
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = String.join(" ", args);
            //channel.basicPublish("", "task_queue",MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Send " + message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
