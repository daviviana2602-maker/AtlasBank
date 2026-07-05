package org.atlas.account;


import jakarta.persistence.*;
import lombok.*;
import org.atlas.transaction.LedgerEntity;
import org.atlas.user.UserEntity;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


@Entity
@Table(name = "account")


public class AccountEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @OneToMany(mappedBy = "account")
    private List<LedgerEntity> ledgers;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private BigDecimal balance = BigDecimal.ZERO;


}