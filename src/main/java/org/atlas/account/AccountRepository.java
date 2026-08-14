package org.atlas.account;


import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountPasswordResetToken(String token);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM AccountEntity a
        WHERE a.user.id = :userId
        """)
    Optional<AccountEntity> findByUserIdWithLock(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM AccountEntity a
        WHERE a.user.cpf = :cpf
        """)
    Optional<AccountEntity> findByUserCpfWithLock(String cpf);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM AccountEntity a
        WHERE a.user.email = :email
        """)
    Optional<AccountEntity> findByUserEmailWithLock(String email);



    @Modifying
    @Query("""
        UPDATE AccountEntity a
        SET 
            a.newAccountPassword = null,
            a.accountPasswordResetToken = null,
            a.accountPasswordResetExpiresAt = null
        WHERE a.accountPasswordResetExpiresAt < :now
    """)
    void clearExpiredNewAccountPasswords(@Param("now") LocalDateTime now);

}