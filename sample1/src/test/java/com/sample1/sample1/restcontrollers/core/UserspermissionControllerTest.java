package com.sample1.sample1.restcontrollers.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample1.sample1.application.core.authorization.permission.PermissionAppService;
import com.sample1.sample1.application.core.authorization.users.UsersAppService;
import com.sample1.sample1.application.core.authorization.userspermission.UserspermissionAppService;
import com.sample1.sample1.application.core.authorization.userspermission.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.SearchUtils;
import com.sample1.sample1.domain.core.authorization.permission.IPermissionRepository;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.IUserspermissionRepository;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionId;
import com.sample1.sample1.domain.core.timesheet.ITimesheetRepository;
import com.sample1.sample1.domain.core.timesheet.TimesheetEntity;
import com.sample1.sample1.domain.core.timesheetstatus.ITimesheetstatusRepository;
import com.sample1.sample1.domain.core.timesheetstatus.TimesheetstatusEntity;
import com.sample1.sample1.security.JWTAppService;
import java.time.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class UserspermissionControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("userspermissionRepository")
    protected IUserspermissionRepository userspermission_repository;

    @Autowired
    @Qualifier("permissionRepository")
    protected IPermissionRepository permissionRepository;

    @Autowired
    @Qualifier("usersRepository")
    protected IUsersRepository usersRepository;

    @Autowired
    @Qualifier("timesheetRepository")
    protected ITimesheetRepository timesheetRepository;

    @Autowired
    @Qualifier("timesheetstatusRepository")
    protected ITimesheetstatusRepository timesheetstatusRepository;

    @SpyBean
    @Qualifier("userspermissionAppService")
    protected UserspermissionAppService userspermissionAppService;

    @SpyBean
    @Qualifier("permissionAppService")
    protected PermissionAppService permissionAppService;

    @SpyBean
    @Qualifier("usersAppService")
    protected UsersAppService usersAppService;

    @SpyBean
    protected JWTAppService jwtAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected UserspermissionEntity userspermission;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    int countTimesheet = 10;

    int countPermission = 10;

    int countUsers = 10;

    int countTimesheetstatus = 10;

    @PostConstruct
    public void init() {
        emfs = emf;
    }

    @AfterClass
    public static void cleanup() {
        EntityManager em = emfs.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("truncate table userspermission RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table permission RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table users RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheet RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheetstatus RESTART IDENTITY").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    public TimesheetEntity createTimesheetEntity() {
        if (countTimesheet > 60) {
            countTimesheet = 10;
        }

        TimesheetEntity timesheetEntity = new TimesheetEntity();
        timesheetEntity.setId(Long.valueOf(relationCount));
        timesheetEntity.setNotes(String.valueOf(relationCount));
        timesheetEntity.setPeriodendingdate(SearchUtils.stringToLocalDate("19" + countTimesheet + "-09-01"));
        timesheetEntity.setPeriodstartingdate(SearchUtils.stringToLocalDate("19" + countTimesheet + "-09-01"));
        timesheetEntity.setVersiono(0L);
        relationCount++;
        TimesheetstatusEntity timesheetstatus = createTimesheetstatusEntity();
        timesheetEntity.setTimesheetstatus(timesheetstatus);
        UsersEntity users = createUsersEntity();
        timesheetEntity.setUsers(users);
        if (!timesheetRepository.findAll().contains(timesheetEntity)) {
            timesheetEntity = timesheetRepository.save(timesheetEntity);
        }
        countTimesheet++;
        return timesheetEntity;
    }

    public PermissionEntity createPermissionEntity() {
        if (countPermission > 60) {
            countPermission = 10;
        }

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setDisplayName(String.valueOf(relationCount));
        permissionEntity.setId(Long.valueOf(relationCount));
        permissionEntity.setName(String.valueOf(relationCount));
        permissionEntity.setVersiono(0L);
        relationCount++;
        if (!permissionRepository.findAll().contains(permissionEntity)) {
            permissionEntity = permissionRepository.save(permissionEntity);
        }
        countPermission++;
        return permissionEntity;
    }

    public UsersEntity createUsersEntity() {
        if (countUsers > 60) {
            countUsers = 10;
        }

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setEmailaddress("bbc" + countUsers + "@d.c");
        usersEntity.setFirstname(String.valueOf(relationCount));
        usersEntity.setId(Long.valueOf(relationCount));
        usersEntity.setIsactive(false);
        usersEntity.setJoinDate(SearchUtils.stringToLocalDate("19" + countUsers + "-09-01"));
        usersEntity.setLastname(String.valueOf(relationCount));
        usersEntity.setPassword(String.valueOf(relationCount));
        usersEntity.setTriggerGroup(String.valueOf(relationCount));
        usersEntity.setTriggerName(String.valueOf(relationCount));
        usersEntity.setUsername(String.valueOf(relationCount));
        usersEntity.setVersiono(0L);
        relationCount++;
        if (!usersRepository.findAll().contains(usersEntity)) {
            usersEntity = usersRepository.save(usersEntity);
        }
        countUsers++;
        return usersEntity;
    }

    public TimesheetstatusEntity createTimesheetstatusEntity() {
        if (countTimesheetstatus > 60) {
            countTimesheetstatus = 10;
        }

        TimesheetstatusEntity timesheetstatusEntity = new TimesheetstatusEntity();
        timesheetstatusEntity.setId(Long.valueOf(relationCount));
        timesheetstatusEntity.setStatusname(String.valueOf(relationCount));
        timesheetstatusEntity.setVersiono(0L);
        relationCount++;
        if (!timesheetstatusRepository.findAll().contains(timesheetstatusEntity)) {
            timesheetstatusEntity = timesheetstatusRepository.save(timesheetstatusEntity);
        }
        countTimesheetstatus++;
        return timesheetstatusEntity;
    }

    public UserspermissionEntity createEntity() {
        PermissionEntity permission = createPermissionEntity();
        UsersEntity users = createUsersEntity();

        UserspermissionEntity userspermissionEntity = new UserspermissionEntity();
        userspermissionEntity.setPermissionId(1L);
        userspermissionEntity.setRevoked(false);
        userspermissionEntity.setUsersId(1L);
        userspermissionEntity.setVersiono(0L);
        userspermissionEntity.setPermission(permission);
        userspermissionEntity.setPermissionId(Long.parseLong(permission.getId().toString()));
        userspermissionEntity.setUsers(users);
        userspermissionEntity.setUsersId(Long.parseLong(users.getId().toString()));

        return userspermissionEntity;
    }

    public CreateUserspermissionInput createUserspermissionInput() {
        CreateUserspermissionInput userspermissionInput = new CreateUserspermissionInput();
        userspermissionInput.setPermissionId(5L);
        userspermissionInput.setRevoked(false);
        userspermissionInput.setUsersId(5L);

        return userspermissionInput;
    }

    public UserspermissionEntity createNewEntity() {
        UserspermissionEntity userspermission = new UserspermissionEntity();
        userspermission.setPermissionId(3L);
        userspermission.setRevoked(false);
        userspermission.setUsersId(3L);

        return userspermission;
    }

    public UserspermissionEntity createUpdateEntity() {
        UserspermissionEntity userspermission = new UserspermissionEntity();
        userspermission.setPermissionId(4L);
        userspermission.setRevoked(false);
        userspermission.setUsersId(4L);

        return userspermission;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final UserspermissionController userspermissionController = new UserspermissionController(
            userspermissionAppService,
            permissionAppService,
            usersAppService,
            jwtAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(userspermissionController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        userspermission = createEntity();
        List<UserspermissionEntity> list = userspermission_repository.findAll();
        if (!list.contains(userspermission)) {
            userspermission = userspermission_repository.save(userspermission);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get(
                    "/userspermission/permissionId=" +
                    userspermission.getPermissionId() +
                    ",usersId=" +
                    userspermission.getUsersId() +
                    "/"
                )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void FindById_IdIsNotValid_ReturnStatusNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/userspermission/permissionId=999,usersId=999").contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreateUserspermission_UserspermissionDoesNotExist_ReturnStatusOk() throws Exception {
        CreateUserspermissionInput userspermissionInput = createUserspermissionInput();

        PermissionEntity permission = createPermissionEntity();

        userspermissionInput.setPermissionId(Long.parseLong(permission.getId().toString()));

        UsersEntity users = createUsersEntity();

        userspermissionInput.setUsersId(Long.parseLong(users.getId().toString()));

        doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(userspermissionInput);

        mvc
            .perform(post("/userspermission").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void CreateUserspermission_permissionDoesNotExists_ThrowEntityNotFoundException() throws Exception {
        CreateUserspermissionInput userspermission = createUserspermissionInput();
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(userspermission);

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                mvc
                    .perform(post("/userspermission").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isNotFound())
        );
    }

    @Test
    public void CreateUserspermission_usersDoesNotExists_ThrowEntityNotFoundException() throws Exception {
        CreateUserspermissionInput userspermission = createUserspermissionInput();
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(userspermission);

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                mvc
                    .perform(post("/userspermission").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isNotFound())
        );
    }

    @Test
    public void DeleteUserspermission_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(userspermissionAppService).findById(new UserspermissionId(999L, 999L));
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            delete("/userspermission/permissionId=999,usersId=999")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(
                new EntityNotFoundException(
                    "There does not exist a userspermission with a id=permissionId=999,usersId=999"
                )
            );
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        UserspermissionEntity entity = createNewEntity();
        entity.setVersiono(0L);
        PermissionEntity permission = createPermissionEntity();
        entity.setPermissionId(Long.parseLong(permission.getId().toString()));
        entity.setPermission(permission);
        UsersEntity users = createUsersEntity();
        entity.setUsersId(Long.parseLong(users.getId().toString()));
        entity.setUsers(users);
        entity = userspermission_repository.save(entity);

        FindUserspermissionByIdOutput output = new FindUserspermissionByIdOutput();
        output.setPermissionId(entity.getPermissionId());
        output.setUsersId(entity.getUsersId());

        //    Mockito.when(userspermissionAppService.findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersId()))).thenReturn(output);
        Mockito
            .doReturn(output)
            .when(userspermissionAppService)
            .findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersId()));

        doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));
        mvc
            .perform(
                delete(
                    "/userspermission/permissionId=" +
                    entity.getPermissionId() +
                    ",usersId=" +
                    entity.getUsersId() +
                    "/"
                )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdateUserspermission_UserspermissionDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(userspermissionAppService).findById(new UserspermissionId(999L, 999L));

        UpdateUserspermissionInput userspermission = new UpdateUserspermissionInput();
        userspermission.setPermissionId(999L);
        userspermission.setRevoked(false);
        userspermission.setUsersId(999L);

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userspermission);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            put("/userspermission/permissionId=999,usersId=999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(
                new EntityNotFoundException(
                    "Unable to update. Userspermission with id=permissionId=999,usersId=999 not found."
                )
            );
    }

    @Test
    public void UpdateUserspermission_UserspermissionExists_ReturnStatusOk() throws Exception {
        UserspermissionEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        PermissionEntity permission = createPermissionEntity();
        entity.setPermissionId(Long.parseLong(permission.getId().toString()));
        entity.setPermission(permission);
        UsersEntity users = createUsersEntity();
        entity.setUsersId(Long.parseLong(users.getId().toString()));
        entity.setUsers(users);
        entity = userspermission_repository.save(entity);
        FindUserspermissionByIdOutput output = new FindUserspermissionByIdOutput();
        output.setPermissionId(entity.getPermissionId());
        output.setRevoked(entity.getRevoked());
        output.setUsersId(entity.getUsersId());
        output.setVersiono(entity.getVersiono());

        Mockito
            .when(
                userspermissionAppService.findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersId()))
            )
            .thenReturn(output);

        UpdateUserspermissionInput userspermissionInput = new UpdateUserspermissionInput();
        userspermissionInput.setPermissionId(entity.getPermissionId());
        userspermissionInput.setUsersId(entity.getUsersId());

        doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userspermissionInput);

        mvc
            .perform(
                put(
                    "/userspermission/permissionId=" +
                    entity.getPermissionId() +
                    ",usersId=" +
                    entity.getUsersId() +
                    "/"
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk());

        UserspermissionEntity de = createUpdateEntity();
        de.setPermissionId(entity.getPermissionId());
        de.setUsersId(entity.getUsersId());
        userspermission_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/userspermission?search=permissionId[equals]=1&limit=10&offset=1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsNotValid_ThrowException() throws Exception {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/userspermission?search=userspermissionpermissionId[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property userspermissionpermissionId not found!"));
    }

    @Test
    public void GetPermission_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/userspermission/permissionId999/permission").contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
    }

    @Test
    public void GetPermission_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/userspermission/permissionId=999,usersId=999/permission")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetPermission_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(
                get(
                    "/userspermission/permissionId=" +
                    userspermission.getPermissionId() +
                    ",usersId=" +
                    userspermission.getUsersId() +
                    "/permission"
                )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void GetUsers_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/userspermission/permissionId999/users").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
    }

    @Test
    public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/userspermission/permissionId=999,usersId=999/users")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(
                get(
                    "/userspermission/permissionId=" +
                    userspermission.getPermissionId() +
                    ",usersId=" +
                    userspermission.getUsersId() +
                    "/users"
                )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }
}
