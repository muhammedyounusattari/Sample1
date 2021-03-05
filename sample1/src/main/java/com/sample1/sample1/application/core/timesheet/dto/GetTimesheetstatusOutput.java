package com.sample1.sample1.application.core.timesheet.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTimesheetstatusOutput {

    private Long id;
    private String statusname;
    private Long timesheetId;
}
