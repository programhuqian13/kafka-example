package org.tony.rabbit.spring.annotation;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tony.rabbit.spring.annotation.config.RabbitClientConfig;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbit.spring.annotation
 */
public class RabbitSpringAnnotationMain {

    public static void main(String... args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitClientConfig.class);
        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
        amqpTemplate.convertAndSend("springAnnotationQueue","Hello world! spring annotation rabbit");
        String message = (String) amqpTemplate.receiveAndConvert("springAnnotationQueue");
        System.out.format("Message:%s",message);
    }


}
