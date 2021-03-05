package com.sample1.sample1.application.extended.authorization.permission;

import com.sample1.sample1.application.core.authorization.permission.PermissionAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("permissionAppServiceExtended")
public class PermissionAppServiceExtended extends PermissionAppService implements IPermissionAppServiceExtended {

    public PermissionAppServiceExtended(
        IPermissionRepositoryExtended permissionRepositoryExtended,
        IPermissionMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(permissionRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
