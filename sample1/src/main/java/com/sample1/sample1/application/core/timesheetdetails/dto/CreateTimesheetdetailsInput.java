package com.sample1.sample1.application.core.timesheetdetails.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CreateTimesheetdetailsInput {

    private Double hours;

    @Length(max = 255, message = "notes must be less than 255 characters")
    private String notes;

    @NotNull(message = "workdate Should not be null")
    private LocalDate workdate;

    private Long taskid;
    private Long timeofftypeid;
    private Long timesheetid;
}
