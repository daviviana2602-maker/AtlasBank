package org.atlas.common.seeder;


import lombok.RequiredArgsConstructor;
import org.atlas.account.AccountEntity;
import org.atlas.account.AccountRepository;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.enums.UserRoleEnum;
import org.atlas.user.enums.UserStatusEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }


        UserEntity atlasAdmin = UserEntity.builder()
                .name("AtlasAdmin")
                .email("atlas@gmail.com")
                .password(passwordEncoder.encode("atlas"))
                .cpf("12345678910")
                .role(UserRoleEnum.ADMIN)
                .status(UserStatusEnum.ACTIVE)
                .emailVerified(true)
                .build();

        userRepository.save(atlasAdmin);


        AccountEntity accountAtlas = AccountEntity.builder()
                        .user(atlasAdmin)
                        .balance(BigDecimal.ZERO)
                        .build();

        accountRepository.save(accountAtlas);


    }

}