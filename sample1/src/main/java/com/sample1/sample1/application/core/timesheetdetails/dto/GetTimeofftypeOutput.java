package com.sample1.sample1.application.core.timesheetdetails.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTimeofftypeOutput {

    private Long id;
    private String typename;
    private Long timesheetdetailsId;
}
