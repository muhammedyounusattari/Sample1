package com.sample1.sample1.domain.core.appconfiguration;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_configuration")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class AppConfigurationEntity extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Basic
    @Column(name = "value", nullable = false, length = 255)
    private String value;
}
