package org.atlas.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<UserEntity> findByEmailVerificationToken(String token);

    Optional<UserEntity> findByPasswordResetToken(String token);

    void deleteByEmailVerifiedFalseAndEmailVerificationExpiresInBefore(LocalDateTime now);


    @Modifying
    @Query("""
        UPDATE UserEntity u
        SET 
            u.newEmail = null,
            u.emailVerificationToken = null,
            u.emailVerificationExpiresIn = null
        WHERE u.emailVerificationExpiresIn < :now
    """)
    void clearExpiredNewEmails(@Param("now") LocalDateTime now);


    @Modifying
    @Query("""
        UPDATE UserEntity u
        SET 
            u.newPassword = null,
            u.passwordResetToken = null,
            u.passwordResetExpiresAt = null
        WHERE u.passwordResetExpiresAt < :now
    """)
    void clearExpiredNewUserPasswords(@Param("now") LocalDateTime now);


}