package org.atlas.auth.service;

import org.atlas.auth.dto.response.CreateAccountResponse;
import org.atlas.common.messaging.event.UserRegisteredEvent;
import org.atlas.common.exception.BadRequestException;
import org.atlas.common.exception.ConflictException;
import org.atlas.common.messaging.producer.UserEventProducer;
import org.atlas.user.UserEntity;
import org.atlas.user.UserRepository;
import org.atlas.user.enums.UserRoleEnum;
import org.atlas.user.enums.UserStatusEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.atlas.common.normalize.StringNormalize.*;


@Service
public class CreateAccountService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;


    public CreateAccountService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                UserEventProducer userEventProducer
    )
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEventProducer = userEventProducer;
    }


    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }


    @Transactional
    public CreateAccountResponse createAccount(String name, String email, String password, String passwordConfirm, String cpf) {


        name = normalizeName(name);
        email = normalizeEmail(email);
        cpf = normalizeCpf(cpf);


        if (existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }
        
        if (existsByCpf(cpf)) {
            throw new ConflictException("Cpf already exists");
        }


        if (name.length() < 3) {
            throw new BadRequestException("Name must be at least 3 characters");
        }


        if (cpf.length() < 11) {
            throw new BadRequestException("CPF number must be at least 11 digits");
        }


        UserEntity user = new UserEntity();

        user.setRole(UserRoleEnum.USER);
        user.setName(name);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setStatus(UserStatusEnum.ACTIVE);


        if (!password.equals(passwordConfirm)) {
            throw new BadRequestException("Passwords do not match");
        }

        String passwordHash = passwordEncoder.encode(password);

        user.setPassword(passwordHash);


        String token = UUID.randomUUID().toString();

        user.setEmailVerified(false);
        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiresIn(LocalDateTime.now().plusMinutes(30));

        
        userRepository.saveAndFlush(user);

        if (user.getId() == 1) {
            user.setRole(UserRoleEnum.ADMIN);
        }


        userEventProducer.publishUserRegistered(new UserRegisteredEvent(email, token));


        return new CreateAccountResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole(),
                user.getCreatedAt()
        );

    }

}