package org.atlas.user;

import jakarta.persistence.*;
import lombok.*;
import org.atlas.account.AccountEntity;
import org.atlas.user.enums.UserRoleEnum;
import org.atlas.user.enums.UserStatusEnum;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Entity
@Table(name = "users")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class UserEntity {

    @OneToOne(mappedBy = "user")
    private AccountEntity account;
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column(unique = true)
    private String cpf;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column
    private String emailVerificationToken;

    @Column
    private LocalDateTime emailVerificationExpiresIn;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;


}