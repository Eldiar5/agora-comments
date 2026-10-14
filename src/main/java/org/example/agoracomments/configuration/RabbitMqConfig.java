package org.example.agoracomments.configuration;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.comments}")
    private String commentsExchange;

    // Создаем обменник, в который будем кидать сообщения
    @Bean
    public TopicExchange commentsExchange() {
        return new TopicExchange(commentsExchange);
    }

    // Конвертер, чтобы отправлять объекты как JSON
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
