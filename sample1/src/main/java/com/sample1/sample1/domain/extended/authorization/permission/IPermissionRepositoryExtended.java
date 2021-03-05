package com.sample1.sample1.domain.extended.authorization.permission;

import com.sample1.sample1.domain.core.authorization.permission.IPermissionRepository;
import org.springframework.stereotype.Repository;

@Repository("permissionRepositoryExtended")
public interface IPermissionRepositoryExtended extends IPermissionRepository {
    //Add your custom code here
}
