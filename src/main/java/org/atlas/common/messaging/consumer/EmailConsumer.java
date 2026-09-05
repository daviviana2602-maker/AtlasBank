package org.atlas.common.messaging.consumer;

import org.atlas.common.messaging.event.UserChangedPasswordEvent;
import org.atlas.common.messaging.event.UserRegisteredEvent;

import org.atlas.common.messaging.RabbitConfig;
import org.atlas.email.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class EmailConsumer {

    private final EmailService emailService;


    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }


    @RabbitListener(queues = RabbitConfig.USER_REGISTERED_QUEUE)
    public void consume(UserRegisteredEvent event) {

        emailService.sendVerificationEmail(
                event.getEmail(),
                event.getToken()
        );

    }


    @RabbitListener(queues = RabbitConfig.USER_CHANGED_PASSWORD_QUEUE)
    public void consume(UserChangedPasswordEvent event) {
        
        emailService.sendEmailUserPassword(
                event.getEmail(),
                event.getToken()
        );

    }


}