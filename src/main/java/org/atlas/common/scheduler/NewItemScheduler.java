package org.atlas.common.scheduler;

import org.atlas.account.AccountRepository;
import org.atlas.user.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
public class NewItemScheduler {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public NewItemScheduler(
            UserRepository userRepository,
            AccountRepository accountRepository
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void clearNewEmails() {
        userRepository.clearExpiredNewEmails(LocalDateTime.now());
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void clearNewUserPassword() {
        userRepository.clearExpiredNewUserPasswords(LocalDateTime.now());
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void clearNewAccountPassword() {
        accountRepository.clearExpiredNewAccountPasswords(LocalDateTime.now());
    }

}