package org.atlas.transaction;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.transaction.dto.request.PixRequest;
import org.atlas.transaction.dto.response.PixResponse;
import org.atlas.transaction.service.PixService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("v1/transaction")
@RequiredArgsConstructor


public class TransactionController {

    private final PixService pixService;


    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public PixResponse sendPix(
        @Valid @RequestBody PixRequest pixRequest
    )
    {
        return pixService.sendPix(
                pixRequest.getToEmail(),
                pixRequest.getToCpf(),
                pixRequest.getDescription(),
                pixRequest.getAmount(),
                pixRequest.getPassword()
        );
    }





}