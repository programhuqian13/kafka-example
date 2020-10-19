package org.tony.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class PublisherConfirms {

    private static final int MESSAGE_COUNT = 50_000;

    static Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        connectionFactory.setPassword("guest");
        connectionFactory.setUsername("guest");

        return connectionFactory.newConnection();
    }

    public static void main(String... args) {
//        publishMessageIndividually();
//        publishMessageInBatch();
        handlePublishConfirmsAsynchronously();
    }

    public static void publishMessageIndividually() {
        try {
            Connection connection = createConnection();
            Channel channel = connection.createChannel();

            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, true, null);
            channel.confirmSelect();
            long start = System.nanoTime();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String body = String.valueOf(i);
                channel.basicPublish("", queue, null, body.getBytes("UTF-8"));
                channel.waitForConfirmsOrDie(5_000);
            }
            long end = System.nanoTime();
            System.out.format("Published %,d message individually in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void publishMessageInBatch() {
        try {
            Connection connection = createConnection();
            Channel channel = connection.createChannel();
            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, true, null);
            channel.confirmSelect();
            int batchSize = 100;
            int outstandingMessageCount = 0;
            long start = System.nanoTime();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String body = String.valueOf(i);
                channel.basicPublish("", queue, null, body.getBytes());
                outstandingMessageCount++;
                if (outstandingMessageCount == batchSize) {
                    channel.waitForConfirmsOrDie(5_000);
                    outstandingMessageCount = 0;
                }
            }
            if (outstandingMessageCount > 0) {
                channel.waitForConfirmsOrDie(5_000);
            }
            long end = System.nanoTime();
            System.out.format("Published %,d messages in batch in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void handlePublishConfirmsAsynchronously() {
        try {
            Connection connection = createConnection();
            Channel channel = connection.createChannel();
            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, true, null);
            channel.confirmSelect();
            ConcurrentNavigableMap<Long,String> outstandingConfirm = new ConcurrentSkipListMap<>();

            ConfirmCallback cleanOutstandingConfirms = (sequeueNumber,multiple)->{
                if(multiple){
                    ConcurrentNavigableMap<Long,String> confirmed
                            = outstandingConfirm.headMap(sequeueNumber,true);
                    confirmed.clear();
                }else{
                    outstandingConfirm.remove(sequeueNumber);
                }
            };
            //添加一个监听
            channel.addConfirmListener(cleanOutstandingConfirms,(sequenceNumber,multiple) -> {
                String body = outstandingConfirm.get(sequenceNumber);
                System.err.format("Message with body %s has been nack-ed." +
                        " Sequence number: %d,multiple: %b%n",body,sequenceNumber,multiple);
                cleanOutstandingConfirms.handle(sequenceNumber,multiple);
            });

            long start = System.nanoTime();
            for(int i = 0; i < MESSAGE_COUNT;i++){
                String body = String.valueOf(i);
                outstandingConfirm.put(channel.getNextPublishSeqNo(),body);
                channel.basicPublish("",queue,null,body.getBytes());
            }

            try {
                if(!waitUntil(Duration.ofSeconds(60),() -> outstandingConfirm.isEmpty())){
                    throw new IllegalStateException("All message could not be confirms in 60 seconds");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long end = System.nanoTime();
            System.out.format("Published %,d message and handled confirms asynchronously in %,d ms%n",MESSAGE_COUNT,Duration.ofNanos(end-start).toMillis());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static boolean waitUntil(Duration duration, BooleanSupplier condition ) throws InterruptedException {
        int waited = 0;
        while(!condition.getAsBoolean() && waited < duration.toMillis()){
            Thread.sleep(100L);
            waited = +100;
        }
        return condition.getAsBoolean();
    }

}
