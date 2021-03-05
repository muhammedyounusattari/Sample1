package com.sample1.sample1.application.extended.usertask;

import com.sample1.sample1.application.core.usertask.UsertaskAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.sample1.sample1.domain.extended.task.ITaskRepositoryExtended;
import com.sample1.sample1.domain.extended.usertask.IUsertaskRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("usertaskAppServiceExtended")
public class UsertaskAppServiceExtended extends UsertaskAppService implements IUsertaskAppServiceExtended {

    public UsertaskAppServiceExtended(
        IUsertaskRepositoryExtended usertaskRepositoryExtended,
        ITaskRepositoryExtended taskRepositoryExtended,
        IUsersRepositoryExtended usersRepositoryExtended,
        IUsertaskMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(usertaskRepositoryExtended, taskRepositoryExtended, usersRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
