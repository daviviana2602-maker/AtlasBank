package org.atlas.transaction;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private TransactionTypeEnum type;

    @Column
    private BigDecimal amount;

    @Column
    @Size(min = 1, max = 100)
    private String description;

    @Column
    private BigDecimal balance_after;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "reference_id")
    private Long referenceId;


}