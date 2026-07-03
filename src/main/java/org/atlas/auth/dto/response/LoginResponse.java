package org.atlas.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atlas.user.enums.UserRoleEnum;


@Getter
@AllArgsConstructor

public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private UserRoleEnum role;
    private String accessToken;
    private String refreshToken;


}