package org.atlas.transaction.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor


public class DepositResponse {

    private Long userId;
    private BigDecimal depositAmount;
    private BigDecimal newBalance;

}
