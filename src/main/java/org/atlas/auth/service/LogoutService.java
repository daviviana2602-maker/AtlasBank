package org.atlas.auth.service;


import org.atlas.auth.repository.RefreshTokenRepository;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.AuthenticatedService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticatedService authenticatedService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public LogoutService(RefreshTokenRepository refreshTokenRepository,
                         AuthenticatedService authenticatedService,
                         PasswordEncoder passwordEncoder,
                         UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticatedService = authenticatedService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    private UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void logoutAccount(String password) {

        Long userId = authenticatedService.getAuthenticatedUserId();

        UserEntity user = findById(userId);


        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ForbiddenException("Invalid password");
        }

        refreshTokenRepository.deleteAllByUserId(user.getId());

    }

}