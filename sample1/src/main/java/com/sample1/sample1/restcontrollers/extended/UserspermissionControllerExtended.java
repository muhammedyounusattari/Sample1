package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.permission.IPermissionAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.users.IUsersAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.userspermission.IUserspermissionAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.UserspermissionController;
import com.sample1.sample1.security.JWTAppService;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userspermission/extended")
public class UserspermissionControllerExtended extends UserspermissionController {

    public UserspermissionControllerExtended(
        IUserspermissionAppServiceExtended userspermissionAppServiceExtended,
        IPermissionAppServiceExtended permissionAppServiceExtended,
        IUsersAppServiceExtended usersAppServiceExtended,
        JWTAppService jwtAppService,
        LoggingHelper helper,
        Environment env
    ) {
        super(
            userspermissionAppServiceExtended,
            permissionAppServiceExtended,
            usersAppServiceExtended,
            jwtAppService,
            helper,
            env
        );
    }
    //Add your custom code here

}
