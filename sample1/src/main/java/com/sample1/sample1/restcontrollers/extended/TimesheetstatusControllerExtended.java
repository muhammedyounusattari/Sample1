package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.timesheet.ITimesheetAppServiceExtended;
import com.sample1.sample1.application.extended.timesheetstatus.ITimesheetstatusAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.TimesheetstatusController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timesheetstatus/extended")
public class TimesheetstatusControllerExtended extends TimesheetstatusController {

    public TimesheetstatusControllerExtended(
        ITimesheetstatusAppServiceExtended timesheetstatusAppServiceExtended,
        ITimesheetAppServiceExtended timesheetAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(timesheetstatusAppServiceExtended, timesheetAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
