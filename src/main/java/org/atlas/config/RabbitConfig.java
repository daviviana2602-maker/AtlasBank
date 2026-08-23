package org.atlas.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String QUEUE = "email.queue";
    public static final String EXCHANGE = "atlas.exchange";

    public static final String ROUTING_KEY = "user.registered";


    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE, true);
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }


    @Bean
    public Binding binding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }


    
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {

        return new Jackson2JsonMessageConverter(objectMapper);

    }

}