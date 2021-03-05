package com.sample1.sample1.domain.core.authorization.role;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionEntity;
import com.sample1.sample1.domain.core.authorization.usersrole.UsersroleEntity;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RoleEntity extends AbstractEntity {

    @Basic
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolepermissionEntity> rolepermissionsSet = new HashSet<RolepermissionEntity>();

    public void addRolepermissions(RolepermissionEntity rolepermissions) {
        rolepermissionsSet.add(rolepermissions);
        rolepermissions.setRole(this);
    }

    public void removeRolepermissions(RolepermissionEntity rolepermissions) {
        rolepermissionsSet.remove(rolepermissions);
        rolepermissions.setRole(null);
    }

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersroleEntity> usersrolesSet = new HashSet<UsersroleEntity>();

    public void addUsersroles(UsersroleEntity usersroles) {
        usersrolesSet.add(usersroles);
        usersroles.setRole(this);
    }

    public void removeUsersroles(UsersroleEntity usersroles) {
        usersrolesSet.remove(usersroles);
        usersroles.setRole(null);
    }
}
