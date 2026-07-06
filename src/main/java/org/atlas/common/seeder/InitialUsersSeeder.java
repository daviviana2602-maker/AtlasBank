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
public class InitialUsersSeeder implements CommandLineRunner {

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
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("atlas"))
                .cpf("16899535009")
                .role(UserRoleEnum.ADMIN)
                .status(UserStatusEnum.ACTIVE)
                .emailVerified(true)
                .build();


        UserEntity atlasUser = UserEntity.builder()
                .name("AtlasUser")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("atlas"))
                .cpf("52998224725")
                .role(UserRoleEnum.USER)
                .status(UserStatusEnum.ACTIVE)
                .emailVerified(true)
                .build();


        userRepository.save(atlasAdmin);
        userRepository.save(atlasUser);


        AccountEntity accountAtlasAdmin = AccountEntity.builder()
                        .user(atlasAdmin)
                        .balance(BigDecimal.TEN)
                        .build();


        AccountEntity accountAtlasUser = AccountEntity.builder()
                        .user(atlasUser)
                        .balance(BigDecimal.TWO)
                        .build();


        accountRepository.save(accountAtlasAdmin);
        accountRepository.save(accountAtlasUser);


    }

}