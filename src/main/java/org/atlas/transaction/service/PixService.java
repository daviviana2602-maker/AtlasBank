package org.atlas.transaction.service;


import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.transaction.entity.LedgerEntity;
import org.atlas.transaction.entity.PixEntity;
import org.atlas.transaction.enums.PixStatusEnum;
import org.atlas.transaction.repository.LedgerRepository;
import org.atlas.transaction.dto.response.PixResponse;
import org.atlas.transaction.enums.TransactionTypeEnum;
import org.atlas.transaction.repository.PixRepository;
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
    private final PixRepository pixRepository;


    public PixService(LedgerRepository ledgerRepository,
                      UserRepository userRepository,
                      AccountRepository accountRepository,
                      PasswordEncoder passwordEncoder,
                      PixRepository pixRepository
    )
    {
        this.ledgerRepository = ledgerRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.pixRepository = pixRepository;
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
        AccountEntity senderAccount = sendingUser.getAccount();


        if (!passwordEncoder.matches(password, senderAccount.getUser().getPassword())) {
            throw new BadRequestException("Wrong password");
        }


        AccountEntity receiverAccount;
        UserEntity userByCpf;
        UserEntity userByEmail;


        if (toCpf != null && !toCpf.isBlank()){

            toCpf = normalizeCpf(toCpf);

            userByCpf = findUserByCpf(toCpf);

            if (toCpf.equals(sendingUser.getCpf())) {
                throw new BadRequestException("You cannot send pix to yourself");
            }

            receiverAccount = findAccountById(userByCpf.getAccount().getId());

        }


        else {

            toEmail = normalizeEmail(toEmail);

            userByEmail = findUserByEmail(toEmail);

            if (toEmail.equals(sendingUser.getEmail())) {
                throw new BadRequestException("You cannot send pix to yourself");
            }

            receiverAccount = findAccountById(userByEmail.getAccount().getId());

        }




        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Amount must be positive");
        }


        if (amount.compareTo(senderAccount.getBalance()) > 0){
            throw new BadRequestException("you do not have enough balance");
        }



        if (description != null) {

            description = normalizeName(description);

            if (description.length() < 1) {
                throw new BadRequestException("description needs to be at least 1 character");
            }

        }


        // Pix transaction
        PixEntity currentPix = new PixEntity();

        currentPix.setSender(senderAccount);
        currentPix.setReceiver(receiverAccount);
        currentPix.setAmount(amount);
        currentPix.setDescription(description);
        currentPix.setStatus(PixStatusEnum.SUCCESS);


        pixRepository.save(currentPix);



        // user who sends the Pix payment (ledger)
        BigDecimal new_balance = senderAccount.getBalance().subtract(amount);

        LedgerEntity transaction = new LedgerEntity();

        transaction.setAccount(senderAccount);
        transaction.setType(TransactionTypeEnum.PIX_SEND);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(new_balance);
        transaction.setPix(currentPix);

        senderAccount.setBalance(new_balance);

        ledgerRepository.save(transaction);



        // user who receives the Pix payment (ledger)
        BigDecimal new_balance_received = receiverAccount.getBalance().add(amount);

        LedgerEntity receiveTransaction = new LedgerEntity();

        receiveTransaction.setAccount(receiverAccount);
        receiveTransaction.setType(TransactionTypeEnum.PIX_RECEIVE);
        receiveTransaction.setAmount(amount);
        receiveTransaction.setBalanceAfter(new_balance_received);
        receiveTransaction.setPix(currentPix);

        receiverAccount.setBalance(new_balance_received);

        ledgerRepository.save(receiveTransaction);


        return new PixResponse(
                currentPix.getDescription(),
                currentPix.getStatus(),
                currentPix.getAmount(),
                currentPix.getCreatedAt()
        );

    }

}