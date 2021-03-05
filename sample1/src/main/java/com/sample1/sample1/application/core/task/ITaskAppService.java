package com.sample1.sample1.application.core.task;

import com.sample1.sample1.application.core.task.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface ITaskAppService {
    //CRUD Operations

    CreateTaskOutput create(CreateTaskInput task);

    void delete(Long id);

    UpdateTaskOutput update(Long id, UpdateTaskInput input);

    FindTaskByIdOutput findById(Long id);

    List<FindTaskByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
    //Relationship Operations

    GetProjectOutput getProject(Long taskid);

    //Join Column Parsers

    Map<String, String> parseTimesheetdetailsJoinColumn(String keysString);

    Map<String, String> parseUsertasksJoinColumn(String keysString);
}
