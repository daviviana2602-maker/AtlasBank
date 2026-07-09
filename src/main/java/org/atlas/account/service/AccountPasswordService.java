package org.atlas.account.service;


import org.atlas.account.AccountRepository;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.ForbiddenException;
import org.atlas.common.exception.NotFoundException;
import org.atlas.security.AuthenticatedService;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AccountPasswordService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedService authenticatedService;


    public AccountPasswordService(AccountRepository accountRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  AuthenticatedService authenticatedService
    ) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedService = authenticatedService;
    }


    private UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Transactional
    public void createAccountPassword(String password) {

        if (!password.matches("\\d{6}")) {
            throw new BadRequestException("Transaction password must contain exactly 6 numbers");
        }

        Long userId = authenticatedService.getAuthenticatedUserId();

        UserEntity user = findById(userId);

        if (user.getAccount().getPassword() != null) {
            throw new BadRequestException("You already have an account password");
        }


        user.getAccount().setPassword(passwordEncoder.encode(password));

    }

}