package org.atlas.auth.service;


import org.atlas.auth.dto.response.LoginResponse;
import org.atlas.auth.entity.RefreshTokenEntity;
import org.atlas.auth.repository.RefreshTokenRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.JwtService;
import org.atlas.security.TokenHashService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.enums.UserStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

import static org.atlas.common.normalize.StringNormalize.normalizeEmail;


@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;
    private final RefreshTokenRepository refreshTokenRepository;


    public LoginService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        TokenHashService tokenHashService,
                        RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMinutes;


    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public LoginResponse systemLogin(String email, String password){

        email = normalizeEmail(email);


        UserEntity user = findUserByEmail(email);

        if (user.getEmailVerified() == false){
            throw new ForbiddenException("Please verify your email before logging in.");
        }

        if (user.getStatus().equals(UserStatusEnum.DISABLED)) {
            throw new ForbiddenException("User is disabled");
        }

        if (user.getStatus().equals(UserStatusEnum.DELETED)) {
            throw new ForbiddenException("User is deleted");
        }


        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("wrong email or password");
        }


        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());

        String hashRefreshToken = tokenHashService.sha256(refreshToken);

        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setUser(user);
        refresh.setTokenHash(hashRefreshToken);
        refresh.setExpiresAt(LocalDateTime.now().plusMinutes(refreshExpirationMinutes));

        refreshTokenRepository.save(refresh);


        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken,
                refreshToken
        );

    }

}