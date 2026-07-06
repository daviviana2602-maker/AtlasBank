package org.atlas.transaction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atlas.account.AccountEntity;
import org.atlas.transaction.enums.TransactionTypeEnum;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "ledger_entries")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class LedgerEntity {


    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "pix_id")
    private PixEntity pix;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    @Column
    private BigDecimal amount;

    @Column(name = "balance_after")
    private BigDecimal balanceAfter;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

}