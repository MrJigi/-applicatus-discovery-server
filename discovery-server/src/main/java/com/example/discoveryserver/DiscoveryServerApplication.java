package com.example.discoveryserver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
@EnableRabbit
@Slf4j
public class DiscoveryServerApplication implements InitializingBean {

//    public static void main(String[] args) {
//        SpringApplication.run(DiscoveryServerApplication.class, args);
//    }

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;


    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitHost);
        connectionFactory.setPort(rabbitPort);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        Queue postDeletedQueue = new Queue("applicatus-deleted-queue", true);
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(postDeletedQueue.getName(), true, false, false, null);
            return null;
        });
        log.info("Queue initialized: post-deleted-queue");
    }
}
