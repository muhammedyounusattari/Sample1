package com.sample1.sample1.restcontrollers.core;

import com.sample1.sample1.application.core.authorization.users.IUsersAppService;
import com.sample1.sample1.application.core.authorization.users.dto.*;
import com.sample1.sample1.application.core.authorization.users.dto.FindUsersByIdOutput;
import com.sample1.sample1.application.core.authorization.userspermission.IUserspermissionAppService;
import com.sample1.sample1.application.core.authorization.userspermission.dto.FindUserspermissionByIdOutput;
import com.sample1.sample1.application.core.authorization.usersrole.IUsersroleAppService;
import com.sample1.sample1.application.core.authorization.usersrole.dto.FindUsersroleByIdOutput;
import com.sample1.sample1.application.core.timesheet.ITimesheetAppService;
import com.sample1.sample1.application.core.timesheet.dto.FindTimesheetByIdOutput;
import com.sample1.sample1.application.core.usertask.IUsertaskAppService;
import com.sample1.sample1.application.core.usertask.dto.FindUsertaskByIdOutput;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.OffsetBasedPageRequest;
import com.sample1.sample1.commons.search.SearchCriteria;
import com.sample1.sample1.commons.search.SearchUtils;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.security.JWTAppService;
import java.time.*;
import java.util.*;
import javax.persistence.EntityExistsException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    @Qualifier("usersAppService")
    @NonNull
    protected final IUsersAppService _usersAppService;

    @Qualifier("timesheetAppService")
    @NonNull
    protected final ITimesheetAppService _timesheetAppService;

    @Qualifier("userspermissionAppService")
    @NonNull
    protected final IUserspermissionAppService _userspermissionAppService;

    @Qualifier("usersroleAppService")
    @NonNull
    protected final IUsersroleAppService _usersroleAppService;

    @Qualifier("usertaskAppService")
    @NonNull
    protected final IUsertaskAppService _usertaskAppService;

    @NonNull
    protected final PasswordEncoder pEncoder;

    @NonNull
    protected final JWTAppService _jwtAppService;

    @NonNull
    protected final LoggingHelper logHelper;

    @NonNull
    protected final Environment env;

    @PreAuthorize("hasAnyAuthority('USERSENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateUsersOutput> create(@RequestBody @Valid CreateUsersInput users) {
        FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(users.getUsername());

        if (foundUsers != null) {
            logHelper.getLogger().error("There already exists a users with a Username=%s", users.getUsername());
            throw new EntityExistsException(
                String.format("There already exists a users with Username =%s", users.getUsername())
            );
        }
        users.setPassword(pEncoder.encode(users.getPassword()));
        foundUsers = _usersAppService.findByEmailaddress(users.getEmailaddress());

        if (foundUsers != null) {
            logHelper.getLogger().error("There already exists a users with a email =%s", users.getEmailaddress());
            throw new EntityExistsException(
                String.format("There already exists a users with email =%s", users.getEmailaddress())
            );
        }

        CreateUsersOutput output = _usersAppService.create(users);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete users ------------
    @PreAuthorize("hasAnyAuthority('USERSENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a users with a id=%s", id))
            );

        _usersAppService.delete(Long.valueOf(id));
    }

    @RequestMapping(
        value = "/updateProfile",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UsersProfile> updateProfile(@RequestBody @Valid UsersProfile usersProfile) {
        UsersEntity users = _usersAppService.getUsers();

        FindUsersByUsernameOutput usersOutput;
        usersOutput = _usersAppService.findByUsername(users.getUsername());
        if (usersOutput != null && usersOutput.getId() != users.getId()) {
            logHelper.getLogger().error("There already exists a users with user name=%s", users.getUsername());
            throw new EntityExistsException(
                String.format("There already exists a users with user name=%s", users.getUsername())
            );
        }
        usersOutput = _usersAppService.findByEmailaddress(users.getEmailaddress());
        if (usersOutput != null && usersOutput.getId() != users.getId()) {
            logHelper.getLogger().error("There already exists a users with a email=%s", users.getEmailaddress());
            throw new EntityExistsException(
                String.format("There already exists a users with a email=%s", users.getEmailaddress())
            );
        }

        FindUsersWithAllFieldsByIdOutput currentUsers = _usersAppService.findWithAllFieldsById(users.getId());
        return new ResponseEntity(_usersAppService.updateUsersProfile(currentUsers, usersProfile), HttpStatus.OK);
    }

    // ------------ Update users ------------
    @PreAuthorize("hasAnyAuthority('USERSENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateUsersOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateUsersInput users
    ) {
        FindUsersWithAllFieldsByIdOutput currentUsers = _usersAppService.findWithAllFieldsById(Long.valueOf(id));
        Optional
            .ofNullable(currentUsers)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Users with id=%s not found.", id))
            );

        users.setPassword(pEncoder.encode(currentUsers.getPassword()));
        if (currentUsers.getIsactive() && !users.getIsactive()) {
            _jwtAppService.deleteAllUserTokens(currentUsers.getUsername());
        }

        users.setVersiono(currentUsers.getVersiono());
        UpdateUsersOutput output = _usersAppService.update(Long.valueOf(id), users);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/getProfile",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UsersProfile> getProfile() {
        UsersEntity users = _usersAppService.getUsers();

        FindUsersByIdOutput currentUsers = _usersAppService.findById(users.getId());
        return new ResponseEntity(_usersAppService.getProfile(currentUsers), HttpStatus.OK);
    }

    @RequestMapping(
        value = "/updateTheme",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<HashMap<String, String>> updateTheme(@RequestParam @Valid String theme) {
        UsersEntity users = _usersAppService.getUsers();
        _usersAppService.updateTheme(users, theme);

        String msg = "Theme updated successfully !";
        HashMap resultMap = new HashMap<String, String>();
        resultMap.put("message", msg);
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/updateLanguage",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<HashMap<String, String>> updateLanguage(@RequestParam @Valid String language) {
        UsersEntity users = _usersAppService.getUsers();
        _usersAppService.updateLanguage(users, language);

        String msg = "Language updated successfully !";
        HashMap resultMap = new HashMap<String, String>();
        resultMap.put("message", msg);
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindUsersByIdOutput> findById(@PathVariable String id) {
        FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(id));
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
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

        return ResponseEntity.ok(_usersAppService.find(searchCriteria, Pageable));
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
    @RequestMapping(
        value = "/{id}/timesheets",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getTimesheets(
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
        Map<String, String> joinColDetails = _usersAppService.parseTimesheetsJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindTimesheetByIdOutput> output = _timesheetAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
    @RequestMapping(
        value = "/{id}/userspermissions",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getUserspermissions(
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
        Map<String, String> joinColDetails = _usersAppService.parseUserspermissionsJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindUserspermissionByIdOutput> output = _userspermissionAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
    @RequestMapping(
        value = "/{id}/usersroles",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getUsersroles(
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
        Map<String, String> joinColDetails = _usersAppService.parseUsersrolesJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindUsersroleByIdOutput> output = _usersroleAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
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
        Map<String, String> joinColDetails = _usersAppService.parseUsertasksJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindUsertaskByIdOutput> output = _usertaskAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
