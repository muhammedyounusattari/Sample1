package com.sample1.sample1.restcontrollers.core;

import com.sample1.sample1.application.core.customer.ICustomerAppService;
import com.sample1.sample1.application.core.project.IProjectAppService;
import com.sample1.sample1.application.core.project.dto.*;
import com.sample1.sample1.application.core.task.ITaskAppService;
import com.sample1.sample1.application.core.task.dto.FindTaskByIdOutput;
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
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    @Qualifier("projectAppService")
    @NonNull
    protected final IProjectAppService _projectAppService;

    @Qualifier("customerAppService")
    @NonNull
    protected final ICustomerAppService _customerAppService;

    @Qualifier("taskAppService")
    @NonNull
    protected final ITaskAppService _taskAppService;

    @NonNull
    protected final LoggingHelper logHelper;

    @NonNull
    protected final Environment env;

    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateProjectOutput> create(@RequestBody @Valid CreateProjectInput project) {
        CreateProjectOutput output = _projectAppService.create(project);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete project ------------
    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        FindProjectByIdOutput output = _projectAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a project with a id=%s", id))
            );

        _projectAppService.delete(Long.valueOf(id));
    }

    // ------------ Update project ------------
    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateProjectOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateProjectInput project
    ) {
        FindProjectByIdOutput currentProject = _projectAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentProject)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Project with id=%s not found.", id))
            );

        project.setVersiono(currentProject.getVersiono());
        UpdateProjectOutput output = _projectAppService.update(Long.valueOf(id), project);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindProjectByIdOutput> findById(@PathVariable String id) {
        FindProjectByIdOutput output = _projectAppService.findById(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_READ')")
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

        return ResponseEntity.ok(_projectAppService.find(searchCriteria, Pageable));
    }

    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_READ')")
    @RequestMapping(
        value = "/{id}/customer",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<GetCustomerOutput> getCustomer(@PathVariable String id) {
        GetCustomerOutput output = _projectAppService.getCustomer(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROJECTENTITY_READ')")
    @RequestMapping(
        value = "/{id}/tasks",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getTasks(
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
        Map<String, String> joinColDetails = _projectAppService.parseTasksJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindTaskByIdOutput> output = _taskAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
