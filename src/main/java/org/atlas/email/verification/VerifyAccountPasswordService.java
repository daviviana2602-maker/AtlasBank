package org.atlas.email.verification;

import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.auth.repository.RefreshTokenRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class VerifyAccountPasswordService {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public VerifyAccountPasswordService(AccountRepository accountRepository, RefreshTokenRepository refreshTokenRepository) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    private AccountEntity getAccountByToken(String token) {
        return accountRepository.findByAccountPasswordResetToken(token)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }


    @Transactional
    public void checkPasswordAccount(String token) {

        AccountEntity account = getAccountByToken(token);


        if (account.getAccountPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {

            throw new BadRequestException("Token expired");

        }


        account.setPassword(account.getNewAccountPassword());

        account.setNewAccountPassword(null);
        account.setAccountPasswordResetToken(null);
        account.setAccountPasswordResetExpiresAt(null);

        refreshTokenRepository.deleteAllByUserId(account.getUser().getId());

    }

}
