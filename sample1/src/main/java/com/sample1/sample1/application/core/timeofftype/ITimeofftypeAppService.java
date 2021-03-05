package com.sample1.sample1.application.core.timeofftype;

import com.sample1.sample1.application.core.timeofftype.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface ITimeofftypeAppService {
    //CRUD Operations

    CreateTimeofftypeOutput create(CreateTimeofftypeInput timeofftype);

    void delete(Long id);

    UpdateTimeofftypeOutput update(Long id, UpdateTimeofftypeInput input);

    FindTimeofftypeByIdOutput findById(Long id);

    List<FindTimeofftypeByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    //Join Column Parsers

    Map<String, String> parseTimesheetdetailsJoinColumn(String keysString);
}
