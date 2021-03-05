package com.sample1.sample1.application.extended.authorization.users;

import com.sample1.sample1.application.core.authorization.users.UsersAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.sample1.sample1.domain.extended.authorization.users.IUsersRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("usersAppServiceExtended")
public class UsersAppServiceExtended extends UsersAppService implements IUsersAppServiceExtended {

    public UsersAppServiceExtended(
        IUsersRepositoryExtended usersRepositoryExtended,
        IUserspreferenceRepository userspreferenceRepository,
        IUsersMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(usersRepositoryExtended, userspreferenceRepository, mapper, logHelper);
    }
    //Add your custom code here

}
