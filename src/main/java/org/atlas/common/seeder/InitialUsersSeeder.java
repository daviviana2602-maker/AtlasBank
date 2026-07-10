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


        UserEntity emailUser = UserEntity.builder()
                .name("EmailUser")
                .email("daviviana2602@gmail.com")
                .password(passwordEncoder.encode("atlas"))
                .cpf("93541134780")
                .role(UserRoleEnum.USER)
                .status(UserStatusEnum.ACTIVE)
                .emailVerified(true)
                .build();


        userRepository.save(atlasAdmin);
        userRepository.save(atlasUser);
        userRepository.save(emailUser);


        AccountEntity accountAtlasAdmin = AccountEntity.builder()
                        .user(atlasAdmin)
                        .balance(BigDecimal.TEN)
                        .password(passwordEncoder.encode("123456"))
                        .build();


        AccountEntity accountAtlasUser = AccountEntity.builder()
                        .user(atlasUser)
                        .balance(BigDecimal.TWO)
                        .password(passwordEncoder.encode("123456"))
                        .build();

        AccountEntity accountEmailUser = AccountEntity.builder()
                        .user(emailUser)
                        .balance(BigDecimal.ONE)
                        .password(passwordEncoder.encode("123456"))
                        .build();


        accountRepository.save(accountAtlasAdmin);
        accountRepository.save(accountAtlasUser);
        accountRepository.save(accountEmailUser);


    }

}