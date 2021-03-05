package com.sample1.sample1.domain.core.authorization.userspermission;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userspermission")
@IdClass(UserspermissionId.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserspermissionEntity extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Basic
    @Column(name = "revoked", nullable = true)
    private Boolean revoked;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private PermissionEntity permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", insertable = false, updatable = false)
    private UsersEntity users;
}
