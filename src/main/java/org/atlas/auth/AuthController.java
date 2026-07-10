package org.atlas.auth;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.auth.dto.request.CreateAccountRequest;
import org.atlas.auth.dto.request.LoginRequest;
import org.atlas.auth.dto.request.LogoutRequest;
import org.atlas.auth.dto.response.CreateAccountResponse;
import org.atlas.auth.dto.response.LoginResponse;
import org.atlas.auth.service.CreateAccountService;
import org.atlas.auth.service.LoginService;
import org.atlas.auth.service.LogoutService;
import org.atlas.auth.service.TokenService;
import org.atlas.email.verification.VerifyAccountPasswordService;
import org.atlas.email.verification.VerifyEmailService;
import org.atlas.email.verification.VerifyUserPasswordService;
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
    private final VerifyUserPasswordService verifyUserPasswordService;
    private final VerifyAccountPasswordService verifyAccountPasswordService;
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


    @GetMapping("/verify-user-password")
    public ResponseEntity<?> verifyUserPassword(
            @RequestParam String token
    ) {
        verifyUserPasswordService.checkPassword(token);

        return ResponseEntity.ok("New password of the user was verified and approved");
    }


    @GetMapping("/verify-account-password")
    public ResponseEntity<?> verifyAccountPassword(
            @RequestParam String token
    ) {
        verifyAccountPasswordService.checkPasswordAccount(token);

        return ResponseEntity.ok("New account password verified and approved");
    }



    @PostMapping("/{refreshToken}")
    public String token(
            @PathVariable String refreshToken
    )
    {
        return tokenService.refreshAccessToken(refreshToken);
    }



}