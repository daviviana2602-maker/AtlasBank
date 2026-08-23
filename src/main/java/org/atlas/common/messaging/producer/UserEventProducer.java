package org.atlas.common.messaging.producer;

import org.atlas.common.messaging.event.UserRegisteredEvent;
import org.atlas.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class UserEventProducer {


    private final RabbitTemplate rabbitTemplate;


    public UserEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishUserRegistered(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                event
        );

    }

}
