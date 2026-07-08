package org.atlas.account.service;


import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.AuthenticatedService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class ListBalanceService {

    private AccountRepository accountRepository;
    private AuthenticatedService authenticadeService;


    public ListBalanceService(AccountRepository accountRepository,
                              AuthenticatedService authenticadeService
    )
    {
        this.accountRepository = accountRepository;
        this.authenticadeService = authenticadeService;
    }


    private AccountEntity findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }


    public BigDecimal getBalance(Long accountId) {

        AccountEntity account = findById(accountId);

        authenticadeService.checkOwnership(accountId);


        return account.getBalance();

    }

}