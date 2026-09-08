package org.atlas.common.messaging.producer;

import org.atlas.common.messaging.RabbitConfig;
import org.atlas.common.messaging.event.UserChangedEmailEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserChangedEmailEventProducer {


    private final RabbitTemplate rabbitTemplate;


    public UserChangedEmailEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishUserChangedEmail(UserChangedEmailEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.ATLAS_EXCHANGE,
                RabbitConfig.USER_CHANGED_EMAIL_ROUTING_KEY,
                event
        );

    }

}