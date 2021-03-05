package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.users.IUsersAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.userspermission.IUserspermissionAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.usersrole.IUsersroleAppServiceExtended;
import com.sample1.sample1.application.extended.timesheet.ITimesheetAppServiceExtended;
import com.sample1.sample1.application.extended.usertask.IUsertaskAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.UsersController;
import com.sample1.sample1.security.JWTAppService;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/extended")
public class UsersControllerExtended extends UsersController {

    public UsersControllerExtended(
        IUsersAppServiceExtended usersAppServiceExtended,
        ITimesheetAppServiceExtended timesheetAppServiceExtended,
        IUserspermissionAppServiceExtended userspermissionAppServiceExtended,
        IUsersroleAppServiceExtended usersroleAppServiceExtended,
        IUsertaskAppServiceExtended usertaskAppServiceExtended,
        PasswordEncoder pEncoder,
        JWTAppService jwtAppService,
        LoggingHelper helper,
        Environment env
    ) {
        super(
            usersAppServiceExtended,
            timesheetAppServiceExtended,
            userspermissionAppServiceExtended,
            usersroleAppServiceExtended,
            usertaskAppServiceExtended,
            pEncoder,
            jwtAppService,
            helper,
            env
        );
    }
    //Add your custom code here

}
