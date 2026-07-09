package org.atlas.account;


import jakarta.persistence.*;
import lombok.*;
import org.atlas.transaction.entity.LedgerEntity;
import org.atlas.transaction.entity.PixEntity;
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


    @OneToMany(mappedBy = "sender")
    private List<PixEntity> sentPixes;

    @OneToMany(mappedBy = "receiver")
    private List<PixEntity> receivedPixes;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column
    private String password;

}