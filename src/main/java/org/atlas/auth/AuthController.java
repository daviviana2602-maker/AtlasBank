package org.atlas.auth;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.auth.dto.request.LoginRequest;
import org.atlas.auth.dto.response.LoginResponse;
import org.atlas.auth.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor


public class AuthController {

    private final LoginService loginService;


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




}