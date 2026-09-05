package org.atlas.account.service;

import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.common.messaging.event.AccountChangedPasswordEvent;
import org.atlas.common.messaging.producer.AccountChangedPasswordEventProducer;
import org.atlas.email.EmailService;
import org.atlas.security.AuthenticatedService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class EditAccountPasswordService {


    private final AccountChangedPasswordEventProducer accountChangedPasswordEventProducer;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedService authenticatedService;


    public EditAccountPasswordService(AccountChangedPasswordEventProducer accountChangedPasswordEventProducer,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      AuthenticatedService authenticatedService
    ) {
        this.accountChangedPasswordEventProducer = accountChangedPasswordEventProducer;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedService = authenticatedService;
    }


    private UserEntity findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void editAccountPassword(String oldPassword, String oldAccountPassword, String newPassword) {

        Long userId =  authenticatedService.getAuthenticatedUserId();

        UserEntity user = findById(userId);


        if (user.getAccount().getPassword() == null){
            throw new ForbiddenException("You need to create a new account password first");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ForbiddenException("The user's old password is incorrect");
        }

        if (!passwordEncoder.matches(oldAccountPassword, user.getAccount().getPassword())) {
            throw new ForbiddenException("The old account password is incorrect");
        }

        if (!newPassword.matches("\\d{6}")) {
            throw new BadRequestException("The new transaction password must contain exactly 6 numbers");
        }


        user.getAccount().setNewAccountPassword(passwordEncoder.encode(newPassword));


        String token = UUID.randomUUID().toString();

        user.getAccount().setAccountPasswordResetToken(token);
        user.getAccount().setAccountPasswordResetExpiresAt(LocalDateTime.now().plusHours(1));

        user.getAccount().setNewAccountPassword(passwordEncoder.encode(newPassword));


        accountChangedPasswordEventProducer.publishAccountChangedPassword(new AccountChangedPasswordEvent(user.getEmail(), token));


    }

}