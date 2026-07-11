package org.atlas.user.service;


import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.AuthenticatedService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class ListBalanceService {

    private UserRepository userRepository;
    private AuthenticatedService authenticadeService;


    public ListBalanceService(UserRepository userRepository, AuthenticatedService authenticadeService)
    {
        this.userRepository = userRepository;
        this.authenticadeService = authenticadeService;
    }


    private UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    public BigDecimal getBalance() {

        Long userId = authenticadeService.getAuthenticatedUserId();

        UserEntity user =  findById(userId);


        return user.getAccount().getBalance();

    }

}