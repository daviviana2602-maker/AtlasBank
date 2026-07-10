package org.atlas.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor


public class EditAccountPasswordRequest {

    @NotBlank(message = "old user's password is required")
    private String userPassword;

    @NotBlank(message = "Old account password is required")
    private String oldAccountPassword;

    @NotBlank(message = "New account assword is required")
    private String newAccountPassword;

}
