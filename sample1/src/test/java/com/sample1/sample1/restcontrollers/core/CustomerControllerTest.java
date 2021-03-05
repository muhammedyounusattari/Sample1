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
import com.sample1.sample1.application.core.customer.CustomerAppService;
import com.sample1.sample1.application.core.customer.dto.*;
import com.sample1.sample1.application.core.project.ProjectAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.SearchUtils;
import com.sample1.sample1.domain.core.customer.CustomerEntity;
import com.sample1.sample1.domain.core.customer.CustomerEntity;
import com.sample1.sample1.domain.core.customer.ICustomerRepository;
import com.sample1.sample1.domain.core.customer.ICustomerRepository;
import com.sample1.sample1.domain.core.project.IProjectRepository;
import com.sample1.sample1.domain.core.project.ProjectEntity;
import com.sample1.sample1.domain.core.task.ITaskRepository;
import com.sample1.sample1.domain.core.task.TaskEntity;
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
public class CustomerControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("customerRepository")
    protected ICustomerRepository customer_repository;

    @Autowired
    @Qualifier("projectRepository")
    protected IProjectRepository projectRepository;

    @Autowired
    @Qualifier("taskRepository")
    protected ITaskRepository taskRepository;

    @Autowired
    @Qualifier("customerRepository")
    protected ICustomerRepository customerRepository;

    @SpyBean
    @Qualifier("customerAppService")
    protected CustomerAppService customerAppService;

    @SpyBean
    @Qualifier("projectAppService")
    protected ProjectAppService projectAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected CustomerEntity customer;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    int countProject = 10;

    int countTask = 10;

    int countCustomer = 10;

    @PostConstruct
    public void init() {
        emfs = emf;
    }

    @AfterClass
    public static void cleanup() {
        EntityManager em = emfs.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("truncate table customer RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table project RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table task RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table customer RESTART IDENTITY").executeUpdate();
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

    public CustomerEntity createEntity() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustomerid(1L);
        customerEntity.setDescription("1");
        customerEntity.setIsactive(false);
        customerEntity.setName("1");
        customerEntity.setVersiono(0L);

        return customerEntity;
    }

    public CreateCustomerInput createCustomerInput() {
        CreateCustomerInput customerInput = new CreateCustomerInput();
        customerInput.setDescription("5");
        customerInput.setIsactive(false);
        customerInput.setName("5");

        return customerInput;
    }

    public CustomerEntity createNewEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerid(3L);
        customer.setDescription("3");
        customer.setIsactive(false);
        customer.setName("3");

        return customer;
    }

    public CustomerEntity createUpdateEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerid(4L);
        customer.setDescription("4");
        customer.setIsactive(false);
        customer.setName("4");

        return customer;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final CustomerController customerController = new CustomerController(
            customerAppService,
            projectAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(customerController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        customer = createEntity();
        List<CustomerEntity> list = customer_repository.findAll();
        if (!list.contains(customer)) {
            customer = customer_repository.save(customer);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(get("/customer/" + customer.getCustomerid() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void FindById_IdIsNotValid_ReturnStatusNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc.perform(get("/customer/999").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreateCustomer_CustomerDoesNotExist_ReturnStatusOk() throws Exception {
        CreateCustomerInput customerInput = createCustomerInput();

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(customerInput);

        mvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void DeleteCustomer_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(customerAppService).findById(999L);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(delete("/customer/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("There does not exist a customer with a id=999"));
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        CustomerEntity entity = createNewEntity();
        entity.setVersiono(0L);
        entity = customer_repository.save(entity);

        FindCustomerByIdOutput output = new FindCustomerByIdOutput();
        output.setCustomerid(entity.getCustomerid());
        output.setIsactive(entity.getIsactive());
        output.setName(entity.getName());

        Mockito.doReturn(output).when(customerAppService).findById(entity.getCustomerid());

        //    Mockito.when(customerAppService.findById(entity.getCustomerid())).thenReturn(output);

        mvc
            .perform(delete("/customer/" + entity.getCustomerid() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdateCustomer_CustomerDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(customerAppService).findById(999L);

        UpdateCustomerInput customer = new UpdateCustomerInput();
        customer.setCustomerid(999L);
        customer.setDescription("999");
        customer.setIsactive(false);
        customer.setName("999");

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(customer);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(put("/customer/999").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Unable to update. Customer with id=999 not found."));
    }

    @Test
    public void UpdateCustomer_CustomerExists_ReturnStatusOk() throws Exception {
        CustomerEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        entity = customer_repository.save(entity);
        FindCustomerByIdOutput output = new FindCustomerByIdOutput();
        output.setCustomerid(entity.getCustomerid());
        output.setDescription(entity.getDescription());
        output.setIsactive(entity.getIsactive());
        output.setName(entity.getName());
        output.setVersiono(entity.getVersiono());

        Mockito.when(customerAppService.findById(entity.getCustomerid())).thenReturn(output);

        UpdateCustomerInput customerInput = new UpdateCustomerInput();
        customerInput.setCustomerid(entity.getCustomerid());
        customerInput.setIsactive(entity.getIsactive());
        customerInput.setName(entity.getName());

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(customerInput);

        mvc
            .perform(
                put("/customer/" + entity.getCustomerid() + "/").contentType(MediaType.APPLICATION_JSON).content(json)
            )
            .andExpect(status().isOk());

        CustomerEntity de = createUpdateEntity();
        de.setCustomerid(entity.getCustomerid());
        customer_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/customer?search=customerid[equals]=1&limit=10&offset=1").contentType(MediaType.APPLICATION_JSON)
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
                            get("/customer?search=customercustomerid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property customercustomerid not found!"));
    }

    @Test
    public void GetProjects_searchIsNotEmptyAndPropertyIsNotValid_ThrowException() {
        Map<String, String> joinCol = new HashMap<String, String>();
        joinCol.put("customerid", "1");

        Mockito.when(customerAppService.parseProjectsJoinColumn("customerid")).thenReturn(joinCol);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/customer/1/projects?search=abc[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property abc not found!"));
    }

    @Test
    public void GetProjects_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        Map<String, String> joinCol = new HashMap<String, String>();
        joinCol.put("customerid", "1");

        Mockito.when(customerAppService.parseProjectsJoinColumn("customerid")).thenReturn(joinCol);
        mvc
            .perform(
                get("/customer/1/projects?search=customerid[equals]=1&limit=10&offset=1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void GetProjects_searchIsNotEmpty() {
        Mockito.when(customerAppService.parseProjectsJoinColumn(anyString())).thenReturn(null);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/customer/1/projects?search=customerid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Invalid join column"));
    }
}
