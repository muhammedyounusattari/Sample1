package com.sample1.sample1.application.extended.timesheetdetails;

import com.sample1.sample1.application.core.timesheetdetails.TimesheetdetailsAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.task.ITaskRepositoryExtended;
import com.sample1.sample1.domain.extended.timeofftype.ITimeofftypeRepositoryExtended;
import com.sample1.sample1.domain.extended.timesheet.ITimesheetRepositoryExtended;
import com.sample1.sample1.domain.extended.timesheetdetails.ITimesheetdetailsRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("timesheetdetailsAppServiceExtended")
public class TimesheetdetailsAppServiceExtended
    extends TimesheetdetailsAppService
    implements ITimesheetdetailsAppServiceExtended {

    public TimesheetdetailsAppServiceExtended(
        ITimesheetdetailsRepositoryExtended timesheetdetailsRepositoryExtended,
        ITaskRepositoryExtended taskRepositoryExtended,
        ITimeofftypeRepositoryExtended timeofftypeRepositoryExtended,
        ITimesheetRepositoryExtended timesheetRepositoryExtended,
        ITimesheetdetailsMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(
            timesheetdetailsRepositoryExtended,
            taskRepositoryExtended,
            timeofftypeRepositoryExtended,
            timesheetRepositoryExtended,
            mapper,
            logHelper
        );
    }
    //Add your custom code here

}
