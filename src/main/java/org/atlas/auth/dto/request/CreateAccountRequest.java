package org.atlas.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor


public class CreateAccountRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "confirm your password")
    private String passwordConfirm;

    @NotBlank(message = "cpf is required")
    @Pattern(regexp = "\\d{11}", message = "add a valid cpf")
    private String cpf;


}