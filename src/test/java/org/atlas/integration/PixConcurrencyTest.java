package org.atlas.integration;

import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.security.AuthenticatedService;
import org.atlas.transaction.repository.LedgerRepository;
import org.atlas.transaction.repository.PixRepository;
import org.atlas.transaction.service.PixService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.enums.UserRoleEnum;
import org.atlas.user.enums.UserStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PixConcurrencyTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("atlas")
                    .withUsername("postgres")
                    .withPassword("testpassword");

    @MockitoBean
    private AuthenticatedService authenticatedService;

    private final PixService pixService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PixRepository pixRepository;
    private final LedgerRepository ledgerRepository;
    private final PasswordEncoder passwordEncoder;



    PixConcurrencyTest(
            PixService pixService,
            AccountRepository accountRepository,
            UserRepository userRepository,
            PixRepository pixRepository,
            LedgerRepository ledgerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.pixService = pixService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.pixRepository = pixRepository;
        this.ledgerRepository = ledgerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Test
    void shouldPreventConcurrentPixFromSpendingSameBalance() throws Exception {


        UserEntity sender = new UserEntity();
        sender.setName("Sender");
        sender.setEmail("sender@test.com");
        sender.setCpf("11111111111");
        sender.setPassword("password");
        sender.setRole(UserRoleEnum.USER);
        sender.setStatus(UserStatusEnum.ACTIVE);

        AccountEntity senderAccount = new AccountEntity();
        senderAccount.setBalance(new BigDecimal("100.00"));
        senderAccount.setPassword(passwordEncoder.encode("123456"));

        senderAccount.setUser(sender);


        UserEntity receiver = new UserEntity();
        receiver.setName("Receiver");
        receiver.setEmail("receiver@test.com");
        receiver.setCpf("22222222222");
        receiver.setPassword("password");
        receiver.setRole(UserRoleEnum.USER);
        receiver.setStatus(UserStatusEnum.ACTIVE);

        AccountEntity receiverAccount = new AccountEntity();
        receiverAccount.setBalance(new BigDecimal("0.00"));

        receiverAccount.setUser(receiver);


        userRepository.save(sender);
        userRepository.save(receiver);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);



        Long senderId = sender.getId();

        when(authenticatedService.getAuthenticatedUserId())
                .thenReturn(senderId);


        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);


        Future<Boolean> firstPix = executor.submit(() -> {

            ready.countDown();
            start.await();

            try {
                pixService.sendPix(
                        "receiver@test.com",
                        null,
                        "PIX 1",
                        new BigDecimal("80.00"),
                        "123456"
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        Future<Boolean> secondPix = executor.submit(() -> {

            ready.countDown();
            start.await();

            try {
                pixService.sendPix(
                        "receiver@test.com",
                        null,
                        "PIX 2",
                        new BigDecimal("80.00"),
                        "123456"
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });


        ready.await();
        start.countDown();

        boolean firstResult = firstPix.get();
        boolean secondResult = secondPix.get();

        executor.shutdown();

        assertTrue(firstResult ^ secondResult);


        AccountEntity finalSender = accountRepository.findById(senderAccount.getId())
                        .orElseThrow();

        AccountEntity finalReceiver = accountRepository.findById(receiverAccount.getId())
                        .orElseThrow();

        assertEquals(new BigDecimal("20.00"), finalSender.getBalance());

        assertEquals(new BigDecimal("80.00"), finalReceiver.getBalance());


        assertEquals(1, pixRepository.count());

        assertEquals(2, ledgerRepository.count());

    }

}