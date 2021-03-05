package com.sample1.sample1.application.extended.authorization.rolepermission;

import com.sample1.sample1.application.core.authorization.rolepermission.RolepermissionAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import com.sample1.sample1.domain.extended.authorization.role.IRoleRepositoryExtended;
import com.sample1.sample1.domain.extended.authorization.rolepermission.IRolepermissionRepositoryExtended;
import com.sample1.sample1.domain.extended.authorization.usersrole.IUsersroleRepositoryExtended;
import com.sample1.sample1.security.JWTAppService;
import org.springframework.stereotype.Service;

@Service("rolepermissionAppServiceExtended")
public class RolepermissionAppServiceExtended
    extends RolepermissionAppService
    implements IRolepermissionAppServiceExtended {

    public RolepermissionAppServiceExtended(
        JWTAppService jwtAppService,
        IUsersroleRepositoryExtended usersroleRepositoryExtended,
        IRolepermissionRepositoryExtended rolepermissionRepositoryExtended,
        IPermissionRepositoryExtended permissionRepositoryExtended,
        IRoleRepositoryExtended roleRepositoryExtended,
        IRolepermissionMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(
            jwtAppService,
            usersroleRepositoryExtended,
            rolepermissionRepositoryExtended,
            permissionRepositoryExtended,
            roleRepositoryExtended,
            mapper,
            logHelper
        );
    }
    //Add your custom code here

}
