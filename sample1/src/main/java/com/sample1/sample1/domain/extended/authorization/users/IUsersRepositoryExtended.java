package com.sample1.sample1.domain.extended.authorization.users;

import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import org.springframework.stereotype.Repository;

@Repository("usersRepositoryExtended")
public interface IUsersRepositoryExtended extends IUsersRepository {
    //Add your custom code here
}
