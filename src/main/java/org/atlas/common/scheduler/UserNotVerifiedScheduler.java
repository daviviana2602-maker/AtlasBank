package org.atlas.common.scheduler;

import org.atlas.user.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
public class UserNotVerifiedScheduler {


    private final UserRepository userRepository;


    public UserNotVerifiedScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteEmailNotVerified() {
        userRepository.deleteByEmailVerifiedFalseAndEmailVerificationExpiresInBefore(LocalDateTime.now());
    }

}
