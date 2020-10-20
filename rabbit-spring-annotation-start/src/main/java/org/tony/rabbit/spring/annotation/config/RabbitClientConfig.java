package org.tony.rabbit.spring.annotation.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbit.spring.annotation.config
 */
@Configuration
public class RabbitClientConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost("10.0.20.196");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return new CachingConnectionFactory(connectionFactory);
    }

    @Bean
    public AmqpAdmin amqpAdmin(){
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue springAnnotationQueue(){
        return new Queue("springAnnotationQueue");
    }
}
