package org.atlas.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atlas.user.enums.UserRoleEnum;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor

public class CreateAccountResponse {

    private Long userId;

    private String name;

    private String email;

    private String cpf;

    private UserRoleEnum role;

    private LocalDateTime createdAt;

}
