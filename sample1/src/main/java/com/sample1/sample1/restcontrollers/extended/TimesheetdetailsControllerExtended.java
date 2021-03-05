package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.task.ITaskAppServiceExtended;
import com.sample1.sample1.application.extended.timeofftype.ITimeofftypeAppServiceExtended;
import com.sample1.sample1.application.extended.timesheet.ITimesheetAppServiceExtended;
import com.sample1.sample1.application.extended.timesheetdetails.ITimesheetdetailsAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.TimesheetdetailsController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timesheetdetails/extended")
public class TimesheetdetailsControllerExtended extends TimesheetdetailsController {

    public TimesheetdetailsControllerExtended(
        ITimesheetdetailsAppServiceExtended timesheetdetailsAppServiceExtended,
        ITaskAppServiceExtended taskAppServiceExtended,
        ITimeofftypeAppServiceExtended timeofftypeAppServiceExtended,
        ITimesheetAppServiceExtended timesheetAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(
            timesheetdetailsAppServiceExtended,
            taskAppServiceExtended,
            timeofftypeAppServiceExtended,
            timesheetAppServiceExtended,
            helper,
            env
        );
    }
    //Add your custom code here

}
