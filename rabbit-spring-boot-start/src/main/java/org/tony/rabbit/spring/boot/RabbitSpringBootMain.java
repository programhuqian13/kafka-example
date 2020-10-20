package org.tony.rabbit.spring.boot;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbit.spring.boot
 */
@SpringBootApplication
public class RabbitSpringBootMain {

    public static void main(String... args) {
        SpringApplication.run(RabbitSpringBootMain.class, args);
    }

    @Bean
    public ApplicationRunner runner(AmqpTemplate template){
        return applicationArguments -> template.convertAndSend("spring-boot-queue","Hello world! spring boot rabbit");
    }

    @Bean
    public Queue queue(){
        return new Queue("spring-boot-queue");
    }

    @RabbitListener(queues = "spring-boot-queue")
    public void listen(String in){
        System.out.println(in);
    }
}
