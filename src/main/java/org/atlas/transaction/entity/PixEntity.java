package org.atlas.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atlas.account.AccountEntity;
import org.atlas.transaction.enums.PixStatusEnum;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@Entity
@Table(name = "pix")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PixEntity {

    @OneToMany(mappedBy = "pix")
    private List<LedgerEntity> ledgers;

    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private AccountEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id")
    private AccountEntity receiver;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal Amount;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private PixStatusEnum status;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

}