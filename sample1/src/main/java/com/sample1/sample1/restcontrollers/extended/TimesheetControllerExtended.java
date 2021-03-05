package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.users.IUsersAppServiceExtended;
import com.sample1.sample1.application.extended.timesheet.ITimesheetAppServiceExtended;
import com.sample1.sample1.application.extended.timesheetdetails.ITimesheetdetailsAppServiceExtended;
import com.sample1.sample1.application.extended.timesheetstatus.ITimesheetstatusAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.TimesheetController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timesheet/extended")
public class TimesheetControllerExtended extends TimesheetController {

    public TimesheetControllerExtended(
        ITimesheetAppServiceExtended timesheetAppServiceExtended,
        ITimesheetdetailsAppServiceExtended timesheetdetailsAppServiceExtended,
        ITimesheetstatusAppServiceExtended timesheetstatusAppServiceExtended,
        IUsersAppServiceExtended usersAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(
            timesheetAppServiceExtended,
            timesheetdetailsAppServiceExtended,
            timesheetstatusAppServiceExtended,
            usersAppServiceExtended,
            helper,
            env
        );
    }
    //Add your custom code here

}
