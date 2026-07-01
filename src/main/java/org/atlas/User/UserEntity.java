package org.atlas.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atlas.common.enums.UserStatusEnum;

import java.time.OffsetDateTime;


@Entity
@Table(name = "users")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String cpf;

    @Column
    private String role;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;

    @Column
    private OffsetDateTime createdAt;


}