package org.atlas.common.messaging.producer;

import org.atlas.common.messaging.RabbitConfig;
import org.atlas.common.messaging.event.UserChangedPasswordEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class UserChangedPasswordEventProducer {


    private final RabbitTemplate rabbitTemplate;


    public UserChangedPasswordEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishUserChangedPassword(UserChangedPasswordEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.ATLAS_EXCHANGE,
                RabbitConfig.USER_CHANGED_PASSWORD_ROUTING_KEY,
                event
        );

    }

}