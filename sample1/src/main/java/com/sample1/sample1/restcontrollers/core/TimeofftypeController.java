package com.sample1.sample1.restcontrollers.core;

import com.sample1.sample1.application.core.timeofftype.ITimeofftypeAppService;
import com.sample1.sample1.application.core.timeofftype.dto.*;
import com.sample1.sample1.application.core.timesheetdetails.ITimesheetdetailsAppService;
import com.sample1.sample1.application.core.timesheetdetails.dto.FindTimesheetdetailsByIdOutput;
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
@RequestMapping("/timeofftype")
@RequiredArgsConstructor
public class TimeofftypeController {

    @Qualifier("timeofftypeAppService")
    @NonNull
    protected final ITimeofftypeAppService _timeofftypeAppService;

    @Qualifier("timesheetdetailsAppService")
    @NonNull
    protected final ITimesheetdetailsAppService _timesheetdetailsAppService;

    @NonNull
    protected final LoggingHelper logHelper;

    @NonNull
    protected final Environment env;

    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateTimeofftypeOutput> create(@RequestBody @Valid CreateTimeofftypeInput timeofftype) {
        CreateTimeofftypeOutput output = _timeofftypeAppService.create(timeofftype);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete timeofftype ------------
    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        FindTimeofftypeByIdOutput output = _timeofftypeAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a timeofftype with a id=%s", id))
            );

        _timeofftypeAppService.delete(Long.valueOf(id));
    }

    // ------------ Update timeofftype ------------
    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateTimeofftypeOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateTimeofftypeInput timeofftype
    ) {
        FindTimeofftypeByIdOutput currentTimeofftype = _timeofftypeAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentTimeofftype)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("Unable to update. Timeofftype with id=%s not found.", id)
                    )
            );

        timeofftype.setVersiono(currentTimeofftype.getVersiono());
        UpdateTimeofftypeOutput output = _timeofftypeAppService.update(Long.valueOf(id), timeofftype);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindTimeofftypeByIdOutput> findById(@PathVariable String id) {
        FindTimeofftypeByIdOutput output = _timeofftypeAppService.findById(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_READ')")
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

        return ResponseEntity.ok(_timeofftypeAppService.find(searchCriteria, Pageable));
    }

    @PreAuthorize("hasAnyAuthority('TIMEOFFTYPEENTITY_READ')")
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
        Map<String, String> joinColDetails = _timeofftypeAppService.parseTimesheetdetailsJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindTimesheetdetailsByIdOutput> output = _timesheetdetailsAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
