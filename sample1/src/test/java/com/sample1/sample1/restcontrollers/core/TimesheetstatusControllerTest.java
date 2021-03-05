package com.sample1.sample1.restcontrollers.core;

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
import com.sample1.sample1.application.core.timesheet.TimesheetAppService;
import com.sample1.sample1.application.core.timesheetstatus.TimesheetstatusAppService;
import com.sample1.sample1.application.core.timesheetstatus.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.SearchUtils;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.timesheet.ITimesheetRepository;
import com.sample1.sample1.domain.core.timesheet.TimesheetEntity;
import com.sample1.sample1.domain.core.timesheetstatus.ITimesheetstatusRepository;
import com.sample1.sample1.domain.core.timesheetstatus.ITimesheetstatusRepository;
import com.sample1.sample1.domain.core.timesheetstatus.TimesheetstatusEntity;
import com.sample1.sample1.domain.core.timesheetstatus.TimesheetstatusEntity;
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
public class TimesheetstatusControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("timesheetstatusRepository")
    protected ITimesheetstatusRepository timesheetstatus_repository;

    @Autowired
    @Qualifier("timesheetRepository")
    protected ITimesheetRepository timesheetRepository;

    @Autowired
    @Qualifier("timesheetstatusRepository")
    protected ITimesheetstatusRepository timesheetstatusRepository;

    @Autowired
    @Qualifier("usersRepository")
    protected IUsersRepository usersRepository;

    @SpyBean
    @Qualifier("timesheetstatusAppService")
    protected TimesheetstatusAppService timesheetstatusAppService;

    @SpyBean
    @Qualifier("timesheetAppService")
    protected TimesheetAppService timesheetAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected TimesheetstatusEntity timesheetstatus;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    int countTimesheet = 10;

    int countTimesheetstatus = 10;

    int countUsers = 10;

    @PostConstruct
    public void init() {
        emfs = emf;
    }

    @AfterClass
    public static void cleanup() {
        EntityManager em = emfs.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("truncate table timesheetstatus RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheet RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheetstatus RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table users RESTART IDENTITY").executeUpdate();
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

    public UsersEntity createUsersEntity() {
        if (countUsers > 60) {
            countUsers = 10;
        }

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setEmailaddress(String.valueOf(relationCount));
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

    public TimesheetstatusEntity createEntity() {
        TimesheetstatusEntity timesheetstatusEntity = new TimesheetstatusEntity();
        timesheetstatusEntity.setId(1L);
        timesheetstatusEntity.setStatusname("1");
        timesheetstatusEntity.setVersiono(0L);

        return timesheetstatusEntity;
    }

    public CreateTimesheetstatusInput createTimesheetstatusInput() {
        CreateTimesheetstatusInput timesheetstatusInput = new CreateTimesheetstatusInput();
        timesheetstatusInput.setStatusname("5");

        return timesheetstatusInput;
    }

    public TimesheetstatusEntity createNewEntity() {
        TimesheetstatusEntity timesheetstatus = new TimesheetstatusEntity();
        timesheetstatus.setId(3L);
        timesheetstatus.setStatusname("3");

        return timesheetstatus;
    }

    public TimesheetstatusEntity createUpdateEntity() {
        TimesheetstatusEntity timesheetstatus = new TimesheetstatusEntity();
        timesheetstatus.setId(4L);
        timesheetstatus.setStatusname("4");

        return timesheetstatus;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final TimesheetstatusController timesheetstatusController = new TimesheetstatusController(
            timesheetstatusAppService,
            timesheetAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(timesheetstatusController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        timesheetstatus = createEntity();
        List<TimesheetstatusEntity> list = timesheetstatus_repository.findAll();
        if (!list.contains(timesheetstatus)) {
            timesheetstatus = timesheetstatus_repository.save(timesheetstatus);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(get("/timesheetstatus/" + timesheetstatus.getId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void FindById_IdIsNotValid_ReturnStatusNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/timesheetstatus/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreateTimesheetstatus_TimesheetstatusDoesNotExist_ReturnStatusOk() throws Exception {
        CreateTimesheetstatusInput timesheetstatusInput = createTimesheetstatusInput();

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(timesheetstatusInput);

        mvc
            .perform(post("/timesheetstatus").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void DeleteTimesheetstatus_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(timesheetstatusAppService).findById(999L);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(delete("/timesheetstatus/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("There does not exist a timesheetstatus with a id=999"));
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        TimesheetstatusEntity entity = createNewEntity();
        entity.setVersiono(0L);
        entity = timesheetstatus_repository.save(entity);

        FindTimesheetstatusByIdOutput output = new FindTimesheetstatusByIdOutput();
        output.setId(entity.getId());
        output.setStatusname(entity.getStatusname());

        Mockito.doReturn(output).when(timesheetstatusAppService).findById(entity.getId());

        //    Mockito.when(timesheetstatusAppService.findById(entity.getId())).thenReturn(output);

        mvc
            .perform(delete("/timesheetstatus/" + entity.getId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdateTimesheetstatus_TimesheetstatusDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(timesheetstatusAppService).findById(999L);

        UpdateTimesheetstatusInput timesheetstatus = new UpdateTimesheetstatusInput();
        timesheetstatus.setId(999L);
        timesheetstatus.setStatusname("999");

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(timesheetstatus);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(put("/timesheetstatus/999").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Unable to update. Timesheetstatus with id=999 not found."));
    }

    @Test
    public void UpdateTimesheetstatus_TimesheetstatusExists_ReturnStatusOk() throws Exception {
        TimesheetstatusEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        entity = timesheetstatus_repository.save(entity);
        FindTimesheetstatusByIdOutput output = new FindTimesheetstatusByIdOutput();
        output.setId(entity.getId());
        output.setStatusname(entity.getStatusname());
        output.setVersiono(entity.getVersiono());

        Mockito.when(timesheetstatusAppService.findById(entity.getId())).thenReturn(output);

        UpdateTimesheetstatusInput timesheetstatusInput = new UpdateTimesheetstatusInput();
        timesheetstatusInput.setId(entity.getId());
        timesheetstatusInput.setStatusname(entity.getStatusname());

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(timesheetstatusInput);

        mvc
            .perform(
                put("/timesheetstatus/" + entity.getId() + "/").contentType(MediaType.APPLICATION_JSON).content(json)
            )
            .andExpect(status().isOk());

        TimesheetstatusEntity de = createUpdateEntity();
        de.setId(entity.getId());
        timesheetstatus_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/timesheetstatus?search=id[equals]=1&limit=10&offset=1").contentType(MediaType.APPLICATION_JSON)
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
                            get("/timesheetstatus?search=timesheetstatusid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property timesheetstatusid not found!"));
    }

    @Test
    public void GetTimesheets_searchIsNotEmptyAndPropertyIsNotValid_ThrowException() {
        Map<String, String> joinCol = new HashMap<String, String>();
        joinCol.put("id", "1");

        Mockito.when(timesheetstatusAppService.parseTimesheetsJoinColumn("timesheetstatusid")).thenReturn(joinCol);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/timesheetstatus/1/timesheets?search=abc[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property abc not found!"));
    }

    @Test
    public void GetTimesheets_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        Map<String, String> joinCol = new HashMap<String, String>();
        joinCol.put("id", "1");

        Mockito.when(timesheetstatusAppService.parseTimesheetsJoinColumn("timesheetstatusid")).thenReturn(joinCol);
        mvc
            .perform(
                get("/timesheetstatus/1/timesheets?search=timesheetstatusid[equals]=1&limit=10&offset=1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void GetTimesheets_searchIsNotEmpty() {
        Mockito.when(timesheetstatusAppService.parseTimesheetsJoinColumn(anyString())).thenReturn(null);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/timesheetstatus/1/timesheets?search=timesheetstatusid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid join column"));
    }
}
