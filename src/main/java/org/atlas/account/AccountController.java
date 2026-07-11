package org.atlas.account;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.account.dto.CreateAccountPasswordRequest;
import org.atlas.account.dto.EditAccountPasswordRequest;
import org.atlas.account.service.AccountPasswordService;
import org.atlas.account.service.EditAccountPasswordService;
import org.atlas.user.service.ListBalanceService;
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
    private final EditAccountPasswordService editAccountPasswordService;



    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public BigDecimal getMoney()
    {
        return listBalanceService.getBalance();
    }


    @PostMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> newAccountPassword(
            @Valid @RequestBody CreateAccountPasswordRequest createAccountPasswordRequest
    )
    {
        accountPasswordService.createAccountPassword(createAccountPasswordRequest.getPassword());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changeAccountPassword(
            @Valid @RequestBody EditAccountPasswordRequest editAccountPasswordRequest
    )
    {
        editAccountPasswordService.editAccountPassword(
                editAccountPasswordRequest.getUserPassword(),
                editAccountPasswordRequest.getOldAccountPassword(),
                editAccountPasswordRequest.getNewAccountPassword()

        );

        return ResponseEntity.noContent().build();
    }

}