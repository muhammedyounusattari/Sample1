package com.sample1.sample1.domain.core.timesheetdetails;

import com.sample1.sample1.domain.core.abstractentity.AbstractEntity;
import com.sample1.sample1.domain.core.task.TaskEntity;
import com.sample1.sample1.domain.core.timeofftype.TimeofftypeEntity;
import com.sample1.sample1.domain.core.timesheet.TimesheetEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "timesheetdetails")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class TimesheetdetailsEntity extends AbstractEntity {

    @Basic
    @Column(name = "hours", nullable = true)
    private Double hours;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "notes", nullable = true, length = 255)
    private String notes;

    @Basic
    @Column(name = "workdate", nullable = false)
    private LocalDate workdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskid")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeofftypeid")
    private TimeofftypeEntity timeofftype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheetid")
    private TimesheetEntity timesheet;
}
