package org.atlas.user;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.user.dto.request.UpdateUserPasswordRequest;
import org.atlas.user.dto.request.UpdateUserRequest;
import org.atlas.user.dto.response.UpdateUserResponse;
import org.atlas.user.service.UpdateUserPasswordService;
import org.atlas.user.service.UpdateUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/user")
@RequiredArgsConstructor


public class UserController {

    private final UpdateUserService updateUserService;
    private final UpdateUserPasswordService updateUserPasswordService;


    @PatchMapping("/")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public UpdateUserResponse updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    )
    {
        return updateUserService.updateUser(
                updateUserRequest.getName(),
                updateUserRequest.getNewEmail()
        );
    }


    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUserPassword(
            @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest
    )
    {
        updateUserPasswordService.changeUserPassword(
                updateUserPasswordRequest.getOldPassword(),
                updateUserPasswordRequest.getNewPassword()
        );

        return ResponseEntity.noContent().build();

    }



}