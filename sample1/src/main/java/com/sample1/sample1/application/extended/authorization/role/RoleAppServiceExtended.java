package com.sample1.sample1.application.extended.authorization.role;

import com.sample1.sample1.application.core.authorization.role.RoleAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.authorization.role.IRoleRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("roleAppServiceExtended")
public class RoleAppServiceExtended extends RoleAppService implements IRoleAppServiceExtended {

    public RoleAppServiceExtended(
        IRoleRepositoryExtended roleRepositoryExtended,
        IRoleMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(roleRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
