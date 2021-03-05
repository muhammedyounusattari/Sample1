package com.sample1.sample1.restcontrollers.core;

import com.sample1.sample1.application.core.appconfiguration.IAppConfigurationAppService;
import com.sample1.sample1.application.core.appconfiguration.dto.*;
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
@RequestMapping("/appConfiguration")
@RequiredArgsConstructor
public class AppConfigurationController {

    @Qualifier("appConfigurationAppService")
    @NonNull
    protected final IAppConfigurationAppService _appConfigurationAppService;

    @NonNull
    protected final LoggingHelper logHelper;

    @NonNull
    protected final Environment env;

    @PreAuthorize("hasAnyAuthority('APPCONFIGURATIONENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateAppConfigurationOutput> create(
        @RequestBody @Valid CreateAppConfigurationInput appConfiguration
    ) {
        CreateAppConfigurationOutput output = _appConfigurationAppService.create(appConfiguration);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete appConfiguration ------------
    @PreAuthorize("hasAnyAuthority('APPCONFIGURATIONENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        FindAppConfigurationByIdOutput output = _appConfigurationAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("There does not exist a appConfiguration with a id=%s", id)
                    )
            );

        _appConfigurationAppService.delete(Long.valueOf(id));
    }

    // ------------ Update appConfiguration ------------
    @PreAuthorize("hasAnyAuthority('APPCONFIGURATIONENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateAppConfigurationOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateAppConfigurationInput appConfiguration
    ) {
        FindAppConfigurationByIdOutput currentAppConfiguration = _appConfigurationAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentAppConfiguration)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("Unable to update. AppConfiguration with id=%s not found.", id)
                    )
            );

        appConfiguration.setVersiono(currentAppConfiguration.getVersiono());
        UpdateAppConfigurationOutput output = _appConfigurationAppService.update(Long.valueOf(id), appConfiguration);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('APPCONFIGURATIONENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindAppConfigurationByIdOutput> findById(@PathVariable String id) {
        FindAppConfigurationByIdOutput output = _appConfigurationAppService.findById(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('APPCONFIGURATIONENTITY_READ')")
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

        return ResponseEntity.ok(_appConfigurationAppService.find(searchCriteria, Pageable));
    }
}
