package org.atlas.transaction.service;


import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.transaction.LedgerEntity;
import org.atlas.transaction.LedgerRepository;
import org.atlas.transaction.dto.response.PixResponse;
import org.atlas.transaction.enums.TransactionTypeEnum;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.atlas.common.normalize.StringNormalize.*;


@Service
public class PixService {


    private final LedgerRepository ledgerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    public PixService(LedgerRepository ledgerRepository,
                      UserRepository userRepository,
                      AccountRepository accountRepository,
                      PasswordEncoder passwordEncoder
    )
    {
        this.ledgerRepository = ledgerRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private AccountEntity findAccountById(Long accountId) {
            return accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Account not found"));
    }


    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }


    private UserEntity findUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("User with cpf: " + cpf + " not found"));
    }


    @Transactional
    public PixResponse sendPix(String toEmail,
                               String toCpf,
                               String description,
                               BigDecimal amount,
                               String password)
    {



        if (toEmail == null && toCpf == null){
            throw new BadRequestException("Add email or cpf");
        }


        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity sendingUser = findUserById(userId);
        AccountEntity account = findAccountById(sendingUser.getAccount().getId());


        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new BadRequestException("Wrong password");
        }


        UserEntity userByEmail = null;
        if (toEmail != null && !toEmail.isBlank()){

            toEmail = normalizeEmail(toEmail);

            userByEmail = findUserByEmail(toEmail);

        }


        UserEntity userByCpf = null;
        if (toCpf != null && !toCpf.isBlank()){

            toCpf = normalizeCpf(toCpf);

            userByCpf = findUserByCpf(toCpf);

        }



        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Amount must be positive");
        }


        if (amount.compareTo(account.getBalance()) > 0){
            throw new BadRequestException("you do not have enough balance");
        }



        if (description != null) {

            description = normalizeName(description);

            if (description.length() < 1) {
                throw new BadRequestException("description needs to be at least 1 character");
            }

        }


        //user who sends the Pix payment
        BigDecimal new_balance = account.getBalance().subtract(amount);

        LedgerEntity transaction = new LedgerEntity();

        transaction.setAccount(account);
        transaction.setDescription(description);
        transaction.setType(TransactionTypeEnum.PIX_SEND);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(new_balance);


        if (userByCpf != null) {
            transaction.setReferenceId(userByCpf.getAccount().getId());
        }

        else  {
            transaction.setReferenceId(userByEmail.getAccount().getId());
        }


        account.setBalance(new_balance);

        ledgerRepository.save(transaction);


        // user who receives the Pix payment
        AccountEntity receive_account = findAccountById(transaction.getReferenceId());

        BigDecimal new_balance_received = receive_account.getBalance().add(amount);

        LedgerEntity receiveTransaction = new LedgerEntity();

        receiveTransaction.setAccount(receive_account);
        receiveTransaction.setDescription(description);
        receiveTransaction.setType(TransactionTypeEnum.PIX_RECEIVE);
        receiveTransaction.setAmount(amount);
        receiveTransaction.setBalanceAfter(new_balance_received);
        receiveTransaction.setReferenceId(transaction.getId());

        receive_account.setBalance(new_balance_received);

        ledgerRepository.save(receiveTransaction);



        return new PixResponse(
                transaction.getDescription(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );

    }

}