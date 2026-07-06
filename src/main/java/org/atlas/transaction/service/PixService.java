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


    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }


    private UserEntity findUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("User with cpf: " + cpf + " not found"));
    }


    private AccountEntity getUser(){

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity sendingUser = findUserById(userId);

        return sendingUser.getAccount();

    }


    private AccountEntity getAccount(String toCpf, String toEmail, AccountEntity senderAccount){

        AccountEntity receiverAccount;
        UserEntity userByCpf;
        UserEntity userByEmail;


        if (toCpf != null && !toCpf.isBlank()){

            toCpf = normalizeCpf(toCpf);

            userByCpf = findUserByCpf(toCpf);

            if (toCpf.equals(senderAccount.getUser().getCpf())) {
                throw new BadRequestException("You cannot send pix to yourself");
            }

            receiverAccount = userByCpf.getAccount();

        }


        else {

            toEmail = normalizeEmail(toEmail);

            userByEmail = findUserByEmail(toEmail);

            if (toEmail.equals(senderAccount.getUser().getEmail())) {
                throw new BadRequestException("You cannot send pix to yourself");
            }

            receiverAccount = userByEmail.getAccount();

        }

        return receiverAccount;

    }


    private PixEntity createPix(AccountEntity senderAccount, AccountEntity receiverAccount, BigDecimal amount, String description) {

        PixEntity currentPix = new PixEntity();

        currentPix.setSender(senderAccount);
        currentPix.setReceiver(receiverAccount);
        currentPix.setAmount(amount);
        currentPix.setDescription(description);
        currentPix.setStatus(PixStatusEnum.SUCCESS);


        pixRepository.save(currentPix);
        return currentPix;

    }


    private void createSenderLedger(AccountEntity senderAccount, BigDecimal amount, PixEntity currentPix) {

        BigDecimal newBalance = senderAccount.getBalance().subtract(amount);

        LedgerEntity transaction = new LedgerEntity();

        transaction.setAccount(senderAccount);
        transaction.setType(TransactionTypeEnum.PIX_SEND);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setPix(currentPix);

        senderAccount.setBalance(newBalance);

        ledgerRepository.save(transaction);

    }


    private void createReceiverLedger(AccountEntity receiverAccount, BigDecimal amount, PixEntity currentPix) {

        BigDecimal newBalanceReceived = receiverAccount.getBalance().add(amount);

        LedgerEntity receiveTransaction = new LedgerEntity();

        receiveTransaction.setAccount(receiverAccount);
        receiveTransaction.setType(TransactionTypeEnum.PIX_RECEIVE);
        receiveTransaction.setAmount(amount);
        receiveTransaction.setBalanceAfter(newBalanceReceived);
        receiveTransaction.setPix(currentPix);

        receiverAccount.setBalance(newBalanceReceived);

        ledgerRepository.save(receiveTransaction);

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


        AccountEntity senderAccount = getUser();

        if (!passwordEncoder.matches(password, senderAccount.getUser().getPassword())) {
            throw new BadRequestException("Wrong password");
        }


        AccountEntity receiverAccount = getAccount(toCpf, toEmail, senderAccount);


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


        PixEntity currentPix = createPix(senderAccount, receiverAccount, amount, description);

        createSenderLedger(senderAccount, amount, currentPix);

        createReceiverLedger(receiverAccount, amount, currentPix);


        return new PixResponse(
                currentPix.getDescription(),
                currentPix.getStatus(),
                currentPix.getAmount(),
                currentPix.getCreatedAt()
        );

    }

}