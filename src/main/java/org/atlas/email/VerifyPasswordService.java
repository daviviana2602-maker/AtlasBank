package org.atlas.email;


import org.atlas.auth.repository.RefreshTokenRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class VerifyPasswordService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public VerifyPasswordService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    private UserEntity getUserByToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void checkPassword(String token) {

        UserEntity user = getUserByToken(token);


        if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {

            throw new BadRequestException("Token expired");

        }


        user.setPassword(user.getNewPassword());

        user.setNewPassword(null);
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);

        refreshTokenRepository.deleteAllByUserId(user.getId());

    }

}