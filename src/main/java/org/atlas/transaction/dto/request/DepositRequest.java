package org.atlas.transaction.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor


public class DepositRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "value needs to be greater than 0")
    @DecimalMax(value = "1000.00", message = "value needs to be between 0.01 - 1000")
    private BigDecimal depositAmount;

}
