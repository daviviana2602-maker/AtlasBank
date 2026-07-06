package org.atlas.transaction.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PixRequest {

    @Email(message = "Email format is wrong")
    private String toEmail;

    @CPF(message = "Cpf format is wrong")
    private String toCpf;

    @Size(max = 70, message = "description needs to be between 1 - 70 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "value needs to be greather than 0")
    private BigDecimal amount;

    @NotBlank(message = "Password is required")
    private String password;

}