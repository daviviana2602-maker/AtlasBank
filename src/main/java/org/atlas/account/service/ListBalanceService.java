package org.atlas.account.service;


import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.OwnershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class ListBalanceService {

    private AccountRepository accountRepository;
    private OwnershipService ownershipService;


    public ListBalanceService(AccountRepository accountRepository,
                              OwnershipService ownershipService
    )
    {
        this.accountRepository = accountRepository;
        this.ownershipService = ownershipService;
    }


    private AccountEntity findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }


    public BigDecimal getBalance(Long accountId) {

        AccountEntity account = findById(accountId);

        ownershipService.checkOwnership(accountId);


        return  account.getBalance();

    }

}