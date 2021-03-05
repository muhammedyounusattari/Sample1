package com.sample1.sample1.application.core.usertask;

import com.sample1.sample1.application.core.usertask.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import com.sample1.sample1.domain.core.usertask.UsertaskId;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface IUsertaskAppService {
    //CRUD Operations

    CreateUsertaskOutput create(CreateUsertaskInput usertask);

    void delete(UsertaskId usertaskId);

    UpdateUsertaskOutput update(UsertaskId usertaskId, UpdateUsertaskInput input);

    FindUsertaskByIdOutput findById(UsertaskId usertaskId);

    List<FindUsertaskByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
    //Relationship Operations
    //Relationship Operations

    GetTaskOutput getTask(UsertaskId usertaskId);

    GetUsersOutput getUsers(UsertaskId usertaskId);

    //Join Column Parsers

    UsertaskId parseUsertaskKey(String keysString);
}
