package org.atlas.common.messaging.consumer;

import org.atlas.common.exception.NotFoundException;
import org.atlas.common.messaging.event.AccountChangedPasswordEvent;
import org.atlas.common.messaging.event.UserChangedEmailEvent;
import org.atlas.common.messaging.event.UserChangedPasswordEvent;
import org.atlas.common.messaging.event.UserRegisteredEvent;

import org.atlas.common.messaging.RabbitConfig;
import org.atlas.email.EmailService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class EmailConsumer {

    private final EmailService emailService;
    private final UserRepository userRepository;


    public EmailConsumer(EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }



    @RabbitListener(queues = RabbitConfig.USER_REGISTERED_QUEUE)
    public void consume(UserRegisteredEvent event) {

        UserEntity user = findUserById(event.getUserId());

        String token;

        if (user.getEmailVerificationToken() == null
            || user.getEmailVerificationExpiresIn() == null
            || user.getEmailVerificationExpiresIn().isBefore(LocalDateTime.now())) {

            token = UUID.randomUUID().toString();

            user.setEmailVerified(false);
            user.setEmailVerificationToken(token);
            user.setEmailVerificationExpiresIn(LocalDateTime.now().plusHours(1));

            userRepository.save(user);

        } else {
            token = user.getEmailVerificationToken();
        }

        emailService.sendVerificationEmail(
                event.getEmail(),
                token
        );

    }


    @RabbitListener(queues = RabbitConfig.USER_CHANGED_PASSWORD_QUEUE)
    public void consume(UserChangedPasswordEvent event) {
        
        emailService.sendEmailUserPassword(
                event.getEmail(),
                event.getToken()
        );

    }


    @RabbitListener(queues = RabbitConfig.ACCOUNT_CHANGED_PASSWORD_QUEUE)
    public void consume(AccountChangedPasswordEvent event) {

        emailService.sendEmailAccountPassword(
                event.getEmail(),
                event.getToken()
        );

    }


    @RabbitListener(queues = RabbitConfig.USER_CHANGED_EMAIL_QUEUE)
    public void consume(UserChangedEmailEvent event) {

        emailService.sendVerificationEmail(
                event.getEmail(),
                event.getToken()
        );

    }


}