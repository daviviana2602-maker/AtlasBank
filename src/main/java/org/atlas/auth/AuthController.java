package org.atlas.auth;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.auth.dto.request.CreateAccountRequest;
import org.atlas.auth.dto.request.LoginRequest;
import org.atlas.auth.dto.response.CreateAccountResponse;
import org.atlas.auth.dto.response.LoginResponse;
import org.atlas.auth.service.CreateAccountService;
import org.atlas.auth.service.LoginService;
import org.atlas.auth.service.TokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor


public class AuthController {

    private final LoginService loginService;
    private final CreateAccountService createAccountService;
    private final TokenService tokenService;


    @PostMapping("/login")
    public LoginResponse createUser(
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


    @PostMapping("/{refreshToken}")
    public String token(
            @PathVariable String refreshToken
    )
    {
        return tokenService.refreshAccessToken(refreshToken);
    }

}