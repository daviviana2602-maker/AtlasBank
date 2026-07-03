package org.atlas.auth.service;

import org.atlas.security.JwtDataFormat;
import org.atlas.security.JwtService;
import org.atlas.user.enums.UserRoleEnum;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtService jwtService;


    public TokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    public String refreshAccessToken(String refreshToken) {

        JwtDataFormat data = jwtService.extractClaims(refreshToken);

        Long userId = Long.valueOf(data.getUserId());
        UserRoleEnum role = UserRoleEnum.valueOf(data.getRole());

        return jwtService.generateAccessToken(userId, role);

    }


}