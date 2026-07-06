package org.atlas.transaction.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.atlas.transaction.enums.PixStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor


public class PixResponse {

    private String description;
    private PixStatusEnum status;
    private BigDecimal Amount;
    private LocalDateTime createdAt;

}
