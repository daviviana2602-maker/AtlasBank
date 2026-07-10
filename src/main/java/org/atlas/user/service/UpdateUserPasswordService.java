package org.atlas.user.service;

import org.atlas.common.exception.BadRequestException;
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
public class UpdateUserPasswordService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedService authenticatedService;
    private final EmailService emailService;


    public UpdateUserPasswordService(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticatedService authenticatedService,
                                     EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedService = authenticatedService;
        this.emailService = emailService;
    }


    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void changeUserPassword(String oldPassword, String newPassword) {

        Long userId = authenticatedService.getAuthenticatedUserId();
        UserEntity user =  findUserById(userId);


        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Current password is wrong");
        }


        String token = UUID.randomUUID().toString();

        emailService.sendEmailUserPassword(user.getEmail(), token);

        user.setPasswordResetToken(token);
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(10));

        user.setNewPassword(passwordEncoder.encode(newPassword));

    }

}