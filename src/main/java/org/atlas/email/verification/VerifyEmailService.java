package org.atlas.email.verification;

import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class VerifyEmailService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public VerifyEmailService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }


    private UserEntity getUserByToken(String token) {
        return userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void verify(String token) {

        UserEntity user = getUserByToken(token);


        if (user.getEmailVerificationExpiresIn()
                .isBefore(LocalDateTime.now())) {

            throw new BadRequestException("Token expired");

        }


        if (user.getNewEmail() != null){

            user.setEmail(user.getNewEmail());

            user.setNewEmail(null);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationExpiresIn(null);

            return;
        }


        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresIn(null);
        user.setEmailVerified(true);


        AccountEntity account = new AccountEntity();

        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);


    }

}