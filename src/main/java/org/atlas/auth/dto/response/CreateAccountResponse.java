package org.atlas.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atlas.user.enums.UserRoleEnum;

import java.time.OffsetDateTime;


@Getter
@AllArgsConstructor

public class CreateAccountResponse {

    private Long userId;

    private String name;

    private String email;

    private String cpf;

    private UserRoleEnum role;

    private OffsetDateTime createdAt;

}
