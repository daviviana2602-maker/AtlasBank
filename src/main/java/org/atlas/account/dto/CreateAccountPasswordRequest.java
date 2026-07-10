package org.atlas.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor


public class CreateAccountPasswordRequest {

    @NotBlank(message = "Password is required")
    private String password;

}