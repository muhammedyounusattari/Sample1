package com.sample1.sample1.domain.core.authorization.userspreference;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userspreference")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserspreferenceEntity extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "language", nullable = false, length = 256)
    private String language;

    @Basic
    @Column(name = "theme", nullable = false, length = 256)
    private String theme;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UsersEntity users;
}
