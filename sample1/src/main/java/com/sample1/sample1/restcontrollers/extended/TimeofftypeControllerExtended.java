package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.timeofftype.ITimeofftypeAppServiceExtended;
import com.sample1.sample1.application.extended.timesheetdetails.ITimesheetdetailsAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.TimeofftypeController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timeofftype/extended")
public class TimeofftypeControllerExtended extends TimeofftypeController {

    public TimeofftypeControllerExtended(
        ITimeofftypeAppServiceExtended timeofftypeAppServiceExtended,
        ITimesheetdetailsAppServiceExtended timesheetdetailsAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(timeofftypeAppServiceExtended, timesheetdetailsAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
