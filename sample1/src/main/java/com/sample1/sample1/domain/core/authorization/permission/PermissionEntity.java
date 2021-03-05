package com.sample1.sample1.domain.core.authorization.permission;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permission")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PermissionEntity extends AbstractEntity {

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

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolepermissionEntity> rolepermissionsSet = new HashSet<RolepermissionEntity>();

    public void addRolepermissions(RolepermissionEntity rolepermissions) {
        rolepermissionsSet.add(rolepermissions);
        rolepermissions.setPermission(this);
    }

    public void removeRolepermissions(RolepermissionEntity rolepermissions) {
        rolepermissionsSet.remove(rolepermissions);
        rolepermissions.setPermission(null);
    }

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserspermissionEntity> userspermissionsSet = new HashSet<UserspermissionEntity>();

    public void addUserspermissions(UserspermissionEntity userspermissions) {
        userspermissionsSet.add(userspermissions);
        userspermissions.setPermission(this);
    }

    public void removeUserspermissions(UserspermissionEntity userspermissions) {
        userspermissionsSet.remove(userspermissions);
        userspermissions.setPermission(null);
    }
}
