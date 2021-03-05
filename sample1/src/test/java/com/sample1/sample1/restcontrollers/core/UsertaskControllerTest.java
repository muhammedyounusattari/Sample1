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
import com.sample1.sample1.application.core.authorization.users.UsersAppService;
import com.sample1.sample1.application.core.task.TaskAppService;
import com.sample1.sample1.application.core.usertask.UsertaskAppService;
import com.sample1.sample1.application.core.usertask.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.SearchUtils;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.customer.CustomerEntity;
import com.sample1.sample1.domain.core.customer.ICustomerRepository;
import com.sample1.sample1.domain.core.project.IProjectRepository;
import com.sample1.sample1.domain.core.project.ProjectEntity;
import com.sample1.sample1.domain.core.task.ITaskRepository;
import com.sample1.sample1.domain.core.task.TaskEntity;
import com.sample1.sample1.domain.core.timesheet.ITimesheetRepository;
import com.sample1.sample1.domain.core.timesheet.TimesheetEntity;
import com.sample1.sample1.domain.core.timesheetstatus.ITimesheetstatusRepository;
import com.sample1.sample1.domain.core.timesheetstatus.TimesheetstatusEntity;
import com.sample1.sample1.domain.core.usertask.IUsertaskRepository;
import com.sample1.sample1.domain.core.usertask.UsertaskEntity;
import com.sample1.sample1.domain.core.usertask.UsertaskId;
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
public class UsertaskControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("usertaskRepository")
    protected IUsertaskRepository usertask_repository;

    @Autowired
    @Qualifier("taskRepository")
    protected ITaskRepository taskRepository;

    @Autowired
    @Qualifier("usersRepository")
    protected IUsersRepository usersRepository;

    @Autowired
    @Qualifier("projectRepository")
    protected IProjectRepository projectRepository;

    @Autowired
    @Qualifier("customerRepository")
    protected ICustomerRepository customerRepository;

    @Autowired
    @Qualifier("timesheetRepository")
    protected ITimesheetRepository timesheetRepository;

    @Autowired
    @Qualifier("timesheetstatusRepository")
    protected ITimesheetstatusRepository timesheetstatusRepository;

    @SpyBean
    @Qualifier("usertaskAppService")
    protected UsertaskAppService usertaskAppService;

    @SpyBean
    @Qualifier("taskAppService")
    protected TaskAppService taskAppService;

    @SpyBean
    @Qualifier("usersAppService")
    protected UsersAppService usersAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected UsertaskEntity usertask;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    int countProject = 10;

    int countTask = 10;

    int countCustomer = 10;

    int countTimesheet = 10;

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

        em.createNativeQuery("truncate table usertask RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table task RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table users RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table project RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table customer RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheet RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table timesheetstatus RESTART IDENTITY").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    public ProjectEntity createProjectEntity() {
        if (countProject > 60) {
            countProject = 10;
        }

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setDescription(String.valueOf(relationCount));
        projectEntity.setEnddate(SearchUtils.stringToLocalDate("19" + countProject + "-09-01"));
        projectEntity.setId(Long.valueOf(relationCount));
        projectEntity.setName(String.valueOf(relationCount));
        projectEntity.setStartdate(SearchUtils.stringToLocalDate("19" + countProject + "-09-01"));
        projectEntity.setVersiono(0L);
        relationCount++;
        CustomerEntity customer = createCustomerEntity();
        projectEntity.setCustomer(customer);
        if (!projectRepository.findAll().contains(projectEntity)) {
            projectEntity = projectRepository.save(projectEntity);
        }
        countProject++;
        return projectEntity;
    }

    public TaskEntity createTaskEntity() {
        if (countTask > 60) {
            countTask = 10;
        }

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDescription(String.valueOf(relationCount));
        taskEntity.setId(Long.valueOf(relationCount));
        taskEntity.setIsactive(false);
        taskEntity.setName(String.valueOf(relationCount));
        taskEntity.setVersiono(0L);
        relationCount++;
        ProjectEntity project = createProjectEntity();
        taskEntity.setProject(project);
        if (!taskRepository.findAll().contains(taskEntity)) {
            taskEntity = taskRepository.save(taskEntity);
        }
        countTask++;
        return taskEntity;
    }

    public CustomerEntity createCustomerEntity() {
        if (countCustomer > 60) {
            countCustomer = 10;
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustomerid(Long.valueOf(relationCount));
        customerEntity.setDescription(String.valueOf(relationCount));
        customerEntity.setIsactive(false);
        customerEntity.setName(String.valueOf(relationCount));
        customerEntity.setVersiono(0L);
        relationCount++;
        if (!customerRepository.findAll().contains(customerEntity)) {
            customerEntity = customerRepository.save(customerEntity);
        }
        countCustomer++;
        return customerEntity;
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

    public UsertaskEntity createEntity() {
        TaskEntity task = createTaskEntity();
        UsersEntity users = createUsersEntity();

        UsertaskEntity usertaskEntity = new UsertaskEntity();
        usertaskEntity.setTaskid(1L);
        usertaskEntity.setUserid(1L);
        usertaskEntity.setVersiono(0L);
        usertaskEntity.setTask(task);
        usertaskEntity.setTaskid(Long.parseLong(task.getId().toString()));
        usertaskEntity.setUsers(users);
        usertaskEntity.setUserid(Long.parseLong(users.getId().toString()));

        return usertaskEntity;
    }

    public CreateUsertaskInput createUsertaskInput() {
        CreateUsertaskInput usertaskInput = new CreateUsertaskInput();
        usertaskInput.setTaskid(5L);
        usertaskInput.setUserid(5L);

        return usertaskInput;
    }

    public UsertaskEntity createNewEntity() {
        UsertaskEntity usertask = new UsertaskEntity();
        usertask.setTaskid(3L);
        usertask.setUserid(3L);

        return usertask;
    }

    public UsertaskEntity createUpdateEntity() {
        UsertaskEntity usertask = new UsertaskEntity();
        usertask.setTaskid(4L);
        usertask.setUserid(4L);

        return usertask;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final UsertaskController usertaskController = new UsertaskController(
            usertaskAppService,
            taskAppService,
            usersAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(usertaskController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        usertask = createEntity();
        List<UsertaskEntity> list = usertask_repository.findAll();
        if (!list.contains(usertask)) {
            usertask = usertask_repository.save(usertask);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/usertask/taskid=" + usertask.getTaskid() + ",userid=" + usertask.getUserid() + "/")
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
                        .perform(get("/usertask/taskid=999,userid=999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreateUsertask_UsertaskDoesNotExist_ReturnStatusOk() throws Exception {
        CreateUsertaskInput usertaskInput = createUsertaskInput();

        TaskEntity task = createTaskEntity();

        usertaskInput.setTaskid(Long.parseLong(task.getId().toString()));

        UsersEntity users = createUsersEntity();

        usertaskInput.setUserid(Long.parseLong(users.getId().toString()));

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(usertaskInput);

        mvc.perform(post("/usertask").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void DeleteUsertask_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(usertaskAppService).findById(new UsertaskId(999L, 999L));
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(delete("/usertask/taskid=999,userid=999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("There does not exist a usertask with a id=taskid=999,userid=999"));
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        UsertaskEntity entity = createNewEntity();
        entity.setVersiono(0L);
        TaskEntity task = createTaskEntity();
        entity.setTaskid(Long.parseLong(task.getId().toString()));
        entity.setTask(task);
        UsersEntity users = createUsersEntity();
        entity.setUserid(Long.parseLong(users.getId().toString()));
        entity.setUsers(users);
        entity = usertask_repository.save(entity);

        FindUsertaskByIdOutput output = new FindUsertaskByIdOutput();
        output.setTaskid(entity.getTaskid());
        output.setUserid(entity.getUserid());

        //    Mockito.when(usertaskAppService.findById(new UsertaskId(entity.getTaskid(), entity.getUserid()))).thenReturn(output);
        Mockito
            .doReturn(output)
            .when(usertaskAppService)
            .findById(new UsertaskId(entity.getTaskid(), entity.getUserid()));

        mvc
            .perform(
                delete("/usertask/taskid=" + entity.getTaskid() + ",userid=" + entity.getUserid() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdateUsertask_UsertaskDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(usertaskAppService).findById(new UsertaskId(999L, 999L));

        UpdateUsertaskInput usertask = new UpdateUsertaskInput();
        usertask.setTaskid(999L);
        usertask.setUserid(999L);

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(usertask);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            put("/usertask/taskid=999,userid=999").contentType(MediaType.APPLICATION_JSON).content(json)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(
                new EntityNotFoundException("Unable to update. Usertask with id=taskid=999,userid=999 not found.")
            );
    }

    @Test
    public void UpdateUsertask_UsertaskExists_ReturnStatusOk() throws Exception {
        UsertaskEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        TaskEntity task = createTaskEntity();
        entity.setTaskid(Long.parseLong(task.getId().toString()));
        entity.setTask(task);
        UsersEntity users = createUsersEntity();
        entity.setUserid(Long.parseLong(users.getId().toString()));
        entity.setUsers(users);
        entity = usertask_repository.save(entity);
        FindUsertaskByIdOutput output = new FindUsertaskByIdOutput();
        output.setTaskid(entity.getTaskid());
        output.setUserid(entity.getUserid());
        output.setVersiono(entity.getVersiono());

        Mockito
            .when(usertaskAppService.findById(new UsertaskId(entity.getTaskid(), entity.getUserid())))
            .thenReturn(output);

        UpdateUsertaskInput usertaskInput = new UpdateUsertaskInput();
        usertaskInput.setTaskid(entity.getTaskid());
        usertaskInput.setUserid(entity.getUserid());

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(usertaskInput);

        mvc
            .perform(
                put("/usertask/taskid=" + entity.getTaskid() + ",userid=" + entity.getUserid() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk());

        UsertaskEntity de = createUpdateEntity();
        de.setTaskid(entity.getTaskid());
        de.setUserid(entity.getUserid());
        usertask_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(get("/usertask?search=taskid[equals]=1&limit=10&offset=1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsNotValid_ThrowException() throws Exception {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/usertask?search=usertasktaskid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property usertasktaskid not found!"));
    }

    @Test
    public void GetTask_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/usertask/taskid999/task").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid id=taskid999"));
    }

    @Test
    public void GetTask_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/usertask/taskid=999,userid=999/task").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetTask_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(
                get("/usertask/taskid=" + usertask.getTaskid() + ",userid=" + usertask.getUserid() + "/task")
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
                        .perform(get("/usertask/taskid999/users").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid id=taskid999"));
    }

    @Test
    public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/usertask/taskid=999,userid=999/users").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(
                get("/usertask/taskid=" + usertask.getTaskid() + ",userid=" + usertask.getUserid() + "/users")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }
}
