package com.sample1.sample1.domain.core.authorization.jwtentity;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jwt_entity")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class JwtEntity extends AbstractEntity {

    @Basic
    @Column(name = "authentication_token", nullable = true)
    private String authenticationToken;

    @Basic
    @Column(name = "authorization_token", nullable = false)
    private String authorizationToken;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "user_name", nullable = false, length = 32)
    private String userName;
}
