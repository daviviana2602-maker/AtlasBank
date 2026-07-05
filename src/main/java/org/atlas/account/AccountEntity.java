package org.atlas.account;


import jakarta.persistence.*;
import lombok.*;
import org.atlas.user.UserEntity;

import java.math.BigDecimal;

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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private BigDecimal balance = BigDecimal.ZERO;


}