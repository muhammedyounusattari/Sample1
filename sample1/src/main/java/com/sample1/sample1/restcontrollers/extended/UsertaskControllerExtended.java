package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.users.IUsersAppServiceExtended;
import com.sample1.sample1.application.extended.task.ITaskAppServiceExtended;
import com.sample1.sample1.application.extended.usertask.IUsertaskAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.UsertaskController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usertask/extended")
public class UsertaskControllerExtended extends UsertaskController {

    public UsertaskControllerExtended(
        IUsertaskAppServiceExtended usertaskAppServiceExtended,
        ITaskAppServiceExtended taskAppServiceExtended,
        IUsersAppServiceExtended usersAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(usertaskAppServiceExtended, taskAppServiceExtended, usersAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
