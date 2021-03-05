package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.role.IRoleAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.rolepermission.IRolepermissionAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.usersrole.IUsersroleAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.RoleController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role/extended")
public class RoleControllerExtended extends RoleController {

    public RoleControllerExtended(
        IRoleAppServiceExtended roleAppServiceExtended,
        IRolepermissionAppServiceExtended rolepermissionAppServiceExtended,
        IUsersroleAppServiceExtended usersroleAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(roleAppServiceExtended, rolepermissionAppServiceExtended, usersroleAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
