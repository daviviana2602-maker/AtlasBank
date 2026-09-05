package org.atlas.common.messaging.producer;

import org.atlas.common.messaging.RabbitConfig;
import org.atlas.common.messaging.event.AccountChangedPasswordEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountChangedPasswordEventProducer {


    private final RabbitTemplate rabbitTemplate;


    public AccountChangedPasswordEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishAccountChangedPassword(AccountChangedPasswordEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.ATLAS_EXCHANGE,
                RabbitConfig.ACCOUNT_CHANGED_PASSWORD_ROUTING_KEY,
                event
        );

    }

}