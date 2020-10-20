package org.tony.rabbit.spring;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbit.spring
 */
public class RabbitXmlMain {

    public static void main(String ... args){
        //获取配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-amqp.xml");
        //获取AmqpTemplate
        AmqpTemplate amqpTemplate = (AmqpTemplate) context.getBean("amqpTemplate");
        //转换消息并且发送
        amqpTemplate.convertAndSend("spring-xml-queue","Hello world spring rabbits");
        //接受并返回消息
        String message = (String) amqpTemplate.receiveAndConvert("spring-xml-queue");
        System.out.format("Result: %s",message);
    }

}
