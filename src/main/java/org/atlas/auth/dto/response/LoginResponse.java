package org.atlas.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atlas.user.enums.UserRoleEnum;


@Getter
@AllArgsConstructor

public class LoginResponse {

    private Long userId;
    private String name;
    private String email;
    private UserRoleEnum role;

}