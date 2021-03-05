package com.sample1.sample1.application.extended.timesheet;

import com.sample1.sample1.application.core.timesheet.TimesheetAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.sample1.sample1.domain.extended.timesheet.ITimesheetRepositoryExtended;
import com.sample1.sample1.domain.extended.timesheetstatus.ITimesheetstatusRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("timesheetAppServiceExtended")
public class TimesheetAppServiceExtended extends TimesheetAppService implements ITimesheetAppServiceExtended {

    public TimesheetAppServiceExtended(
        ITimesheetRepositoryExtended timesheetRepositoryExtended,
        ITimesheetstatusRepositoryExtended timesheetstatusRepositoryExtended,
        IUsersRepositoryExtended usersRepositoryExtended,
        ITimesheetMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(
            timesheetRepositoryExtended,
            timesheetstatusRepositoryExtended,
            usersRepositoryExtended,
            mapper,
            logHelper
        );
    }
    //Add your custom code here

}
