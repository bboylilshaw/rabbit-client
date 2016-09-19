package com.example;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Jason Xiao
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private static final String EXCHANGE = "my.exchange";
    private static final String ROUTING_KEY = "my.routing.key";
    private static final String QUEUE = "my.queue";
    private static final String REPLY_QUEUE = "my.reply.queue";

    @Bean
    public AmqpAdmin admin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        Queue queue = new Queue(QUEUE, true);
        Queue replyQueue = new Queue(REPLY_QUEUE, true);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareQueue(replyQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY));
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(EXCHANGE);
        rabbitTemplate.setRoutingKey(ROUTING_KEY);
        rabbitTemplate.setQueue(QUEUE);
        rabbitTemplate.setReplyTimeout(15000); // timeout in 15 seconds
        return rabbitTemplate;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(25);
        taskExecutor.setQueueCapacity(1000);
        return taskExecutor;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                                   MessageConverter messageConverter,
                                                                   TaskExecutor taskExecutor) {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        messageListenerContainer.setMessageConverter(messageConverter);
        messageListenerContainer.setTaskExecutor(taskExecutor);
        messageListenerContainer.setQueueNames(REPLY_QUEUE);
        return messageListenerContainer;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, SimpleMessageListenerContainer container) {
        return new AsyncRabbitTemplate(rabbitTemplate, container, REPLY_QUEUE);
    }

}
