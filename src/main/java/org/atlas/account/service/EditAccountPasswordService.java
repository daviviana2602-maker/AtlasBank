package org.atlas.account.service;

import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
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


    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedService authenticatedService;


    public EditAccountPasswordService(EmailService emailService,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      AuthenticatedService authenticatedService
    ) {
        this.emailService = emailService;
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

        emailService.sendEmailAccountPassword(user.getEmail(), token);

        user.getAccount().setAccountPasswordResetToken(token);
        user.getAccount().setAccountPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(10));

        user.getAccount().setNewAccountPassword(passwordEncoder.encode(newPassword));


    }

}