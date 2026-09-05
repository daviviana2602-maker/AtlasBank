package org.atlas.common.messaging;

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

    public static final String ATLAS_EXCHANGE = "atlas.exchange";

    public static final String USER_REGISTERED_QUEUE = "user.registered.queue";
    public static final String USER_CHANGED_PASSWORD_QUEUE = "user.changed.password.queue";
    public static final String ACCOUNT_CHANGED_PASSWORD_QUEUE = "account.changed.password.queue";

    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";
    public static final String USER_CHANGED_PASSWORD_ROUTING_KEY = "user.changed.password";
    public static final String ACCOUNT_CHANGED_PASSWORD_ROUTING_KEY = "account.changed.password";


    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_REGISTERED_QUEUE, true);
    }


    @Bean
    public Queue userChangedPasswordQueue() {
        return new Queue(USER_CHANGED_PASSWORD_QUEUE, true);
    }

    @Bean
    public Queue accountChangedPasswordQueue() {
        return new Queue(ACCOUNT_CHANGED_PASSWORD_QUEUE, true);
    }



    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(ATLAS_EXCHANGE);
    }


    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(exchange)
                .with(USER_REGISTERED_ROUTING_KEY);
    }


    @Bean
    public Binding userChangedPasswordBinding(Queue userChangedPasswordQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(userChangedPasswordQueue)
                .to(exchange)
                .with(USER_CHANGED_PASSWORD_ROUTING_KEY);
    }


    @Bean
    public Binding accountChangedPasswordBinding(Queue accountChangedPasswordQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(accountChangedPasswordQueue)
                .to(exchange)
                .with(ACCOUNT_CHANGED_PASSWORD_ROUTING_KEY);
    }




    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {

        return new Jackson2JsonMessageConverter(objectMapper);

    }

}