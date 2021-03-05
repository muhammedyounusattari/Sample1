package com.sample1.sample1.application.core.timesheet;

import com.sample1.sample1.application.core.timesheet.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface ITimesheetAppService {
    //CRUD Operations

    CreateTimesheetOutput create(CreateTimesheetInput timesheet);

    void delete(Long id);

    UpdateTimesheetOutput update(Long id, UpdateTimesheetInput input);

    FindTimesheetByIdOutput findById(Long id);

    List<FindTimesheetByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
    //Relationship Operations
    //Relationship Operations

    GetTimesheetstatusOutput getTimesheetstatus(Long timesheetid);

    GetUsersOutput getUsers(Long timesheetid);

    //Join Column Parsers

    Map<String, String> parseTimesheetdetailsJoinColumn(String keysString);
}
