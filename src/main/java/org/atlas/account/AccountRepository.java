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
    @Query("SELECT account FROM AccountEntity account WHERE account.id = :id")
    Optional<AccountEntity> findByIdWithLock(Long id);


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