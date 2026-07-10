package org.atlas.auth;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.account.service.ListBalanceService;
import org.atlas.auth.dto.request.CreateAccountRequest;
import org.atlas.auth.dto.request.LoginRequest;
import org.atlas.auth.dto.request.LogoutRequest;
import org.atlas.auth.dto.response.CreateAccountResponse;
import org.atlas.auth.dto.response.LoginResponse;
import org.atlas.auth.service.CreateAccountService;
import org.atlas.auth.service.LoginService;
import org.atlas.auth.service.LogoutService;
import org.atlas.auth.service.TokenService;
import org.atlas.email.VerifyEmailService;
import org.atlas.email.VerifyPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor


public class AuthController {

    private final LoginService loginService;
    private final CreateAccountService createAccountService;
    private final TokenService tokenService;
    private final VerifyEmailService verifyEmailService;
    private final VerifyPasswordService verifyPasswordService;
    private final LogoutService logoutService;


    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest loginRequest
    )
    {
        return loginService.systemLogin(

                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }


    @PostMapping("/create")
    public CreateAccountResponse createUser(
            @Valid @RequestBody CreateAccountRequest createAccountRequest
    )
    {
        return createAccountService.createAccount(

                createAccountRequest.getName(),
                createAccountRequest.getEmail(),
                createAccountRequest.getPassword(),
                createAccountRequest.getPasswordConfirm(),
                createAccountRequest.getCpf()
        );
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> leaveUser(
            @Valid @RequestBody LogoutRequest logoutRequest
    )
    {
        logoutService.logoutAccount(logoutRequest.getPassword());

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String token
    ) {
        verifyEmailService.verify(token);

        return ResponseEntity.ok("Email verified");
    }


    @GetMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(
            @RequestParam String token
    ) {
        verifyPasswordService.checkPassword(token);

        return ResponseEntity.ok("New password verified and approved");
    }


    @PostMapping("/{refreshToken}")
    public String token(
            @PathVariable String refreshToken
    )
    {
        return tokenService.refreshAccessToken(refreshToken);
    }


}