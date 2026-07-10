package org.atlas.user.service;


import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.email.EmailService;
import org.atlas.security.AuthenticatedService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.dto.response.UpdateUserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.atlas.common.normalize.StringNormalize.normalizeEmail;
import static org.atlas.common.normalize.StringNormalize.normalizeName;


@Service
public class UpdateUserService {

    private final UserRepository userRepository;
    private final AuthenticatedService authenticatedService;
    private final EmailService emailService;


    public UpdateUserService(UserRepository userRepository,
                             AuthenticatedService authenticatedService,
                             EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticatedService = authenticatedService;
        this.emailService = emailService;
    }


    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }



    @Transactional
    public UpdateUserResponse updateUser(String name, String newEmail) {


    if (name == null && newEmail == null) {
        throw new BadRequestException("at least one field is required");
    }


    if (name != null) {

        name = normalizeName(name);

        if (name.length() < 3) {
                    throw new BadRequestException("name must be at least 3 characters");
        }
    }


    Long userId = authenticatedService.getAuthenticatedUserId();
    UserEntity user = findUserById(userId);


    if (name != null) {
        user.setName(name);
    }


    if (newEmail != null) {

        newEmail = normalizeEmail(newEmail);

        if (newEmail.isBlank()) {
                    throw new BadRequestException("Email cannot be empty");
        }


        if (userRepository.existsByEmail(newEmail)) {
                    throw new BadRequestException("Email already exists");
        }


        String token = UUID.randomUUID().toString();

        emailService.sendVerificationEmail(newEmail, token);

        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiresIn(LocalDateTime.now().plusMinutes(10));

        user.setNewEmail(newEmail);

    }


    return new UpdateUserResponse(
            user.getName(),
            user.getEmail(),
            user.getNewEmail()
    );

    }

}