package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.authorization.permission.IPermissionAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.rolepermission.IRolepermissionAppServiceExtended;
import com.sample1.sample1.application.extended.authorization.userspermission.IUserspermissionAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.PermissionController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission/extended")
public class PermissionControllerExtended extends PermissionController {

    public PermissionControllerExtended(
        IPermissionAppServiceExtended permissionAppServiceExtended,
        IRolepermissionAppServiceExtended rolepermissionAppServiceExtended,
        IUserspermissionAppServiceExtended userspermissionAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(
            permissionAppServiceExtended,
            rolepermissionAppServiceExtended,
            userspermissionAppServiceExtended,
            helper,
            env
        );
    }
    //Add your custom code here

}
