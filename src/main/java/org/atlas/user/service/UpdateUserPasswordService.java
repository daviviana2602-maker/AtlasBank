package org.atlas.user.service;

import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.common.messaging.event.UserChangedPasswordEvent;
import org.atlas.common.messaging.producer.UserChangedPasswordEventProducer;
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
    private final UserChangedPasswordEventProducer userChangedPasswordEventProducer;


    public UpdateUserPasswordService(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticatedService authenticatedService,
                                     UserChangedPasswordEventProducer userChangedPasswordEventProducer
                                     ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedService = authenticatedService;
        this.userChangedPasswordEventProducer = userChangedPasswordEventProducer;
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

        user.setPasswordResetToken(token);
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(1));

        user.setNewPassword(passwordEncoder.encode(newPassword));

        userChangedPasswordEventProducer.publishUserChangedPassword(new UserChangedPasswordEvent(user.getEmail(), token));

    }

}