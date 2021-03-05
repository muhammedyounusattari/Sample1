package com.sample1.sample1.application.core.timesheet.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateTimesheetInput {

    @NotNull(message = "id Should not be null")
    private Long id;

    @Length(max = 255, message = "notes must be less than 255 characters")
    private String notes;

    @NotNull(message = "periodendingdate Should not be null")
    private LocalDate periodendingdate;

    @NotNull(message = "periodstartingdate Should not be null")
    private LocalDate periodstartingdate;

    private Long timesheetstatusid;
    private Long userid;
    private Long versiono;
}
