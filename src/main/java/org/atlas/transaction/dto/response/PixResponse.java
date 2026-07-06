package org.atlas.transaction.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.atlas.transaction.enums.TransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor


public class PixResponse {

    private String description;
    private TransactionTypeEnum type;
    private BigDecimal amount;
    private BigDecimal balance_after;
    private LocalDateTime createdAt;

}
