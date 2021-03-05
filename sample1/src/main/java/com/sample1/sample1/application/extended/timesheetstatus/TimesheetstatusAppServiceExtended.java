package com.sample1.sample1.application.extended.timesheetstatus;

import com.sample1.sample1.application.core.timesheetstatus.TimesheetstatusAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.timesheetstatus.ITimesheetstatusRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("timesheetstatusAppServiceExtended")
public class TimesheetstatusAppServiceExtended
    extends TimesheetstatusAppService
    implements ITimesheetstatusAppServiceExtended {

    public TimesheetstatusAppServiceExtended(
        ITimesheetstatusRepositoryExtended timesheetstatusRepositoryExtended,
        ITimesheetstatusMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(timesheetstatusRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
