package com.sample1.sample1.application.core.authorization.role;

import com.sample1.sample1.application.core.authorization.role.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface IRoleAppService {
    //CRUD Operations

    CreateRoleOutput create(CreateRoleInput role);

    void delete(Long id);

    UpdateRoleOutput update(Long id, UpdateRoleInput input);

    FindRoleByIdOutput findById(Long id);

    List<FindRoleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    FindRoleByNameOutput findByRoleName(String roleName);

    //Join Column Parsers

    Map<String, String> parseRolepermissionsJoinColumn(String keysString);

    Map<String, String> parseUsersrolesJoinColumn(String keysString);
}
