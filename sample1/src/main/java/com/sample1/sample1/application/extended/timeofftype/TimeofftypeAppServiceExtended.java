package com.sample1.sample1.application.extended.timeofftype;

import com.sample1.sample1.application.core.timeofftype.TimeofftypeAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.timeofftype.ITimeofftypeRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("timeofftypeAppServiceExtended")
public class TimeofftypeAppServiceExtended extends TimeofftypeAppService implements ITimeofftypeAppServiceExtended {

    public TimeofftypeAppServiceExtended(
        ITimeofftypeRepositoryExtended timeofftypeRepositoryExtended,
        ITimeofftypeMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(timeofftypeRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
