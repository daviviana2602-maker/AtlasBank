package org.atlas.transaction.service;


import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.AuthenticatedService;
import org.atlas.transaction.dto.response.DepositResponse;
import org.atlas.transaction.entity.LedgerEntity;
import org.atlas.transaction.enums.TransactionTypeEnum;
import org.atlas.transaction.repository.LedgerRepository;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class DepositService {

    private final UserRepository userRepository;
    private final LedgerRepository ledgerRepository;
    private final AuthenticatedService authenticatedService;


    public DepositService(UserRepository userRepository,
                          LedgerRepository ledgerRepository,
                          AuthenticatedService authenticatedService) {
        this.userRepository = userRepository;
        this.ledgerRepository = ledgerRepository;
        this.authenticatedService = authenticatedService;
    }


    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    private void createDepositLedger(UserEntity user, BigDecimal newBalance, BigDecimal depositAmount) {

        LedgerEntity depositLedger = new LedgerEntity();

        depositLedger.setAccount(user.getAccount());
        depositLedger.setType(TransactionTypeEnum.DEPOSIT);
        depositLedger.setAmount(depositAmount);
        depositLedger.setBalanceAfter(newBalance);

        ledgerRepository.save(depositLedger);

    }


    @Transactional
    public DepositResponse deposit(BigDecimal depositAmount) {

        Long userId = authenticatedService.getAuthenticatedUserId();

        UserEntity user =  findUserById(userId);

        if (user.getAccount().getPassword() == null) {
            throw new ForbiddenException("Account password is required");
        }


        BigDecimal newBalance = user.getAccount().getBalance().add(depositAmount);

        user.getAccount().setBalance(newBalance);


        createDepositLedger(user,newBalance, depositAmount);


        return new DepositResponse(
                userId,
                depositAmount,
                user.getAccount().getBalance()
        );

    }

}