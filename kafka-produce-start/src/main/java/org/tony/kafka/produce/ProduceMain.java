package org.tony.kafka.produce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.tony.kafka.produce.topic.TopicService;

import java.util.Properties;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.kafka.produce
 */
public class ProduceMain {

    public static void main(String... args) {
        //首先创建topic
//        TopicService.createTopics();

        //创建消息提供者的相关配置信息
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.0.20.197:9092");
        properties.put("acks", "all");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //创建一个kafka的消息提供者
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i = 0; i < 100; i++) {
            //发送消息到kafka
            producer.send(new ProducerRecord<String, String>("topic-test",Integer.toString(1),Integer.toString(i)));
        }

        producer.close();
    }

}
