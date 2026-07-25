package org.atlas.auth.service;

import org.atlas.auth.entity.RefreshTokenEntity;
import org.atlas.auth.repository.RefreshTokenRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.JwtDataFormat;
import org.atlas.security.JwtService;
import org.atlas.security.TokenHashService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.enums.UserRoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class TokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;


    public TokenService(JwtService jwtService,
                        RefreshTokenRepository refreshTokenRepository,
                        TokenHashService tokenHashService
    ) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHashService = tokenHashService;
    }


    private RefreshTokenEntity findByTokenHash(String hashRefreshToken) {
        return refreshTokenRepository.findByTokenHash(hashRefreshToken)
                .orElseThrow(() -> new NotFoundException("Token doesn't exists"));
    }


    public ResponseEntity refreshAccessToken(String refreshToken) {


        JwtDataFormat data = jwtService.extractClaims(refreshToken);

        if (!"REFRESH".equals(data.getType())) {
            throw new BadRequestException("Refresh token is required");
        }


        String hashRefreshToken = tokenHashService.sha256(refreshToken);

        RefreshTokenEntity refresh = findByTokenHash(hashRefreshToken);

        if (LocalDateTime.now().isAfter(refresh.getExpiresAt())) {
            throw new BadRequestException("Token has expired");
        }


        Long userId = Long.valueOf(data.getUserId());
        UserRoleEnum role = UserRoleEnum.valueOf(data.getRole());

        String accessToken = jwtService.generateAccessToken(userId, role);


        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();

    }

}