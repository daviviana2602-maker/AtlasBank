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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PixConcurrencyTest {


    @MockitoBean
    private AuthenticatedService authenticatedService;

    private final PixService pixService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PixRepository pixRepository;
    private final LedgerRepository ledgerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;



    PixConcurrencyTest(
            PixService pixService,
            AccountRepository accountRepository,
            UserRepository userRepository,
            PixRepository pixRepository,
            LedgerRepository ledgerRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate
    ) {
        this.pixService = pixService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.pixRepository = pixRepository;
        this.ledgerRepository = ledgerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("atlas")
                    .withUsername("postgres")
                    .withPassword("testpassword");


    @BeforeEach
    void cleanDatabase() throws Exception {
        jdbcTemplate.execute("""
        TRUNCATE TABLE ledger_entries, pix, account, users
        RESTART IDENTITY CASCADE
    """);
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



    @Test
    void shouldPreventConcurrentPixAgainstDeadlock() throws Exception {


        UserEntity senderOne = new UserEntity();
        senderOne.setName("SenderOne");
        senderOne.setEmail("senderone@test.com");
        senderOne.setCpf("11111111111");
        senderOne.setPassword("password");
        senderOne.setRole(UserRoleEnum.USER);
        senderOne.setStatus(UserStatusEnum.ACTIVE);

        AccountEntity senderOneAccount = new AccountEntity();
        senderOneAccount.setBalance(new BigDecimal("50.00"));
        senderOneAccount.setPassword(passwordEncoder.encode("123456"));

        senderOneAccount.setUser(senderOne);


        UserEntity senderTwo = new UserEntity();
        senderTwo.setName("SenderTwo");
        senderTwo.setEmail("sendertwo@test.com");
        senderTwo.setCpf("22222222222");
        senderTwo.setPassword("password");
        senderTwo.setRole(UserRoleEnum.USER);
        senderTwo.setStatus(UserStatusEnum.ACTIVE);

        AccountEntity senderTwoAccount = new AccountEntity();
        senderTwoAccount.setBalance(new BigDecimal("25.00"));
        senderTwoAccount.setPassword(passwordEncoder.encode("123456"));

        senderTwoAccount.setUser(senderTwo);


        userRepository.save(senderOne);
        userRepository.save(senderTwo);

        accountRepository.save(senderOneAccount);
        accountRepository.save(senderTwoAccount);


        when(authenticatedService.getAuthenticatedUserId())
                .thenAnswer(invocation -> {

                    String threadName = Thread.currentThread().getName();

                    if (threadName.equals("pix-1")) {
                        return senderOne.getId();
                    }

                    return senderTwo.getId();
                });


        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);


        AtomicInteger threadNumber = new AtomicInteger(1);

        ExecutorService executor = Executors.newFixedThreadPool(2, task -> {
            Thread thread = new Thread(task);
            thread.setName("pix-" + threadNumber.getAndIncrement());    // new name to the threads: "pix-1" and "pix-2"
            return thread;
        });


        Future<Boolean> firstPix = executor.submit(() -> {

            ready.countDown();
            start.await();

            try {
                pixService.sendPix(
                        "sendertwo@test.com",
                        null,
                        "PIX 1",
                        new BigDecimal("20.00"),
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
                        "senderone@test.com",
                        null,
                        "PIX 2",
                        new BigDecimal("20.00"),
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

        assertTrue(firstResult && secondResult);


        AccountEntity firstSenderFinal = accountRepository.findById(senderOneAccount.getId())
                .orElseThrow();

        AccountEntity secondSenderFinal = accountRepository.findById(senderTwoAccount.getId())
                .orElseThrow();


        assertEquals(new BigDecimal("50.00"), firstSenderFinal.getBalance());

        assertEquals(new BigDecimal("25.00"), secondSenderFinal.getBalance());


        assertEquals(2, pixRepository.count());

        assertEquals(4, ledgerRepository.count());

    }

}