package org.atlas.account;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.account.dto.AccountPasswordRequest;
import org.atlas.account.service.AccountPasswordService;
import org.atlas.account.service.ListBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor


public class AccountController {

    private final ListBalanceService listBalanceService;
    private final AccountPasswordService accountPasswordService;



    @GetMapping("/{accountId}/balance")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public BigDecimal getMoney(
            @PathVariable Long accountId
    )
    {
        return listBalanceService.getBalance(accountId);
    }


    @PostMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> accountPassword(
            @Valid @RequestBody AccountPasswordRequest accountPasswordRequest
    )
    {
        accountPasswordService.createAccountPassword(accountPasswordRequest.getPassword());

        return ResponseEntity.noContent().build();
    }


}