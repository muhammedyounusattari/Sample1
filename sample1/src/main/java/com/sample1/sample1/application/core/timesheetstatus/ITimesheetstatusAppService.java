package com.sample1.sample1.application.core.timesheetstatus;

import com.sample1.sample1.application.core.timesheetstatus.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface ITimesheetstatusAppService {
    //CRUD Operations

    CreateTimesheetstatusOutput create(CreateTimesheetstatusInput timesheetstatus);

    void delete(Long id);

    UpdateTimesheetstatusOutput update(Long id, UpdateTimesheetstatusInput input);

    FindTimesheetstatusByIdOutput findById(Long id);

    List<FindTimesheetstatusByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    //Join Column Parsers

    Map<String, String> parseTimesheetsJoinColumn(String keysString);
}
