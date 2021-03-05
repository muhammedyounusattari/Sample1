package com.sample1.sample1.restcontrollers.core;

import com.sample1.sample1.application.core.project.IProjectAppService;
import com.sample1.sample1.application.core.task.ITaskAppService;
import com.sample1.sample1.application.core.task.dto.*;
import com.sample1.sample1.application.core.timesheetdetails.ITimesheetdetailsAppService;
import com.sample1.sample1.application.core.timesheetdetails.dto.FindTimesheetdetailsByIdOutput;
import com.sample1.sample1.application.core.usertask.IUsertaskAppService;
import com.sample1.sample1.application.core.usertask.dto.FindUsertaskByIdOutput;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.OffsetBasedPageRequest;
import com.sample1.sample1.commons.search.SearchCriteria;
import com.sample1.sample1.commons.search.SearchUtils;
import java.time.*;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    @Qualifier("taskAppService")
    @NonNull
    protected final ITaskAppService _taskAppService;

    @Qualifier("projectAppService")
    @NonNull
    protected final IProjectAppService _projectAppService;

    @Qualifier("timesheetdetailsAppService")
    @NonNull
    protected final ITimesheetdetailsAppService _timesheetdetailsAppService;

    @Qualifier("usertaskAppService")
    @NonNull
    protected final IUsertaskAppService _usertaskAppService;

    @NonNull
    protected final LoggingHelper logHelper;

    @NonNull
    protected final Environment env;

    @PreAuthorize("hasAnyAuthority('TASKENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateTaskOutput> create(@RequestBody @Valid CreateTaskInput task) {
        CreateTaskOutput output = _taskAppService.create(task);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete task ------------
    @PreAuthorize("hasAnyAuthority('TASKENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        FindTaskByIdOutput output = _taskAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a task with a id=%s", id))
            );

        _taskAppService.delete(Long.valueOf(id));
    }

    // ------------ Update task ------------
    @PreAuthorize("hasAnyAuthority('TASKENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateTaskOutput> update(@PathVariable String id, @RequestBody @Valid UpdateTaskInput task) {
        FindTaskByIdOutput currentTask = _taskAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentTask)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Task with id=%s not found.", id))
            );

        task.setVersiono(currentTask.getVersiono());
        UpdateTaskOutput output = _taskAppService.update(Long.valueOf(id), task);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TASKENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindTaskByIdOutput> findById(@PathVariable String id) {
        FindTaskByIdOutput output = _taskAppService.findById(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TASKENTITY_READ')")
    @RequestMapping(method = RequestMethod.GET, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity find(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) throws Exception {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

        return ResponseEntity.ok(_taskAppService.find(searchCriteria, Pageable));
    }

    @PreAuthorize("hasAnyAuthority('TASKENTITY_READ')")
    @RequestMapping(
        value = "/{id}/project",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<GetProjectOutput> getProject(@PathVariable String id) {
        GetProjectOutput output = _taskAppService.getProject(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TASKENTITY_READ')")
    @RequestMapping(
        value = "/{id}/timesheetdetails",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getTimesheetdetails(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) throws Exception {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
        Map<String, String> joinColDetails = _taskAppService.parseTimesheetdetailsJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindTimesheetdetailsByIdOutput> output = _timesheetdetailsAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TASKENTITY_READ')")
    @RequestMapping(
        value = "/{id}/usertasks",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getUsertasks(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) throws Exception {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
        Map<String, String> joinColDetails = _taskAppService.parseUsertasksJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindUsertaskByIdOutput> output = _usertaskAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
