package org.atlas.account;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.atlas.account.service.ListBalanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor


public class AccountController {

    private final ListBalanceService listBalanceService;



    @GetMapping("/{accountId}/balance")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public BigDecimal getMoney(
            @PathVariable Long accountId
    )
    {
        return listBalanceService.getBalance(accountId);
    }



}