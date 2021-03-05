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
import com.sample1.sample1.application.core.appconfiguration.AppConfigurationAppService;
import com.sample1.sample1.application.core.appconfiguration.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.core.appconfiguration.AppConfigurationEntity;
import com.sample1.sample1.domain.core.appconfiguration.IAppConfigurationRepository;
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
public class AppConfigurationControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("appConfigurationRepository")
    protected IAppConfigurationRepository appConfiguration_repository;

    @SpyBean
    @Qualifier("appConfigurationAppService")
    protected AppConfigurationAppService appConfigurationAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected AppConfigurationEntity appConfiguration;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    @PostConstruct
    public void init() {
        emfs = emf;
    }

    @AfterClass
    public static void cleanup() {
        EntityManager em = emfs.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("truncate table app_configuration RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    public AppConfigurationEntity createEntity() {
        AppConfigurationEntity appConfigurationEntity = new AppConfigurationEntity();
        appConfigurationEntity.setId(1L);
        appConfigurationEntity.setType("1");
        appConfigurationEntity.setValue("1");
        appConfigurationEntity.setVersiono(0L);

        return appConfigurationEntity;
    }

    public CreateAppConfigurationInput createAppConfigurationInput() {
        CreateAppConfigurationInput appConfigurationInput = new CreateAppConfigurationInput();
        appConfigurationInput.setType("5");
        appConfigurationInput.setValue("5");

        return appConfigurationInput;
    }

    public AppConfigurationEntity createNewEntity() {
        AppConfigurationEntity appConfiguration = new AppConfigurationEntity();
        appConfiguration.setId(3L);
        appConfiguration.setType("3");
        appConfiguration.setValue("3");

        return appConfiguration;
    }

    public AppConfigurationEntity createUpdateEntity() {
        AppConfigurationEntity appConfiguration = new AppConfigurationEntity();
        appConfiguration.setId(4L);
        appConfiguration.setType("4");
        appConfiguration.setValue("4");

        return appConfiguration;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final AppConfigurationController appConfigurationController = new AppConfigurationController(
            appConfigurationAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(appConfigurationController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        appConfiguration = createEntity();
        List<AppConfigurationEntity> list = appConfiguration_repository.findAll();
        if (!list.contains(appConfiguration)) {
            appConfiguration = appConfiguration_repository.save(appConfiguration);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(get("/appConfiguration/" + appConfiguration.getId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void FindById_IdIsNotValid_ReturnStatusNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/appConfiguration/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreateAppConfiguration_AppConfigurationDoesNotExist_ReturnStatusOk() throws Exception {
        CreateAppConfigurationInput appConfigurationInput = createAppConfigurationInput();

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(appConfigurationInput);

        mvc
            .perform(post("/appConfiguration").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void DeleteAppConfiguration_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(appConfigurationAppService).findById(999L);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(delete("/appConfiguration/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("There does not exist a appConfiguration with a id=999"));
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        AppConfigurationEntity entity = createNewEntity();
        entity.setVersiono(0L);
        entity = appConfiguration_repository.save(entity);

        FindAppConfigurationByIdOutput output = new FindAppConfigurationByIdOutput();
        output.setId(entity.getId());
        output.setType(entity.getType());
        output.setValue(entity.getValue());

        Mockito.doReturn(output).when(appConfigurationAppService).findById(entity.getId());

        //    Mockito.when(appConfigurationAppService.findById(entity.getId())).thenReturn(output);

        mvc
            .perform(delete("/appConfiguration/" + entity.getId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdateAppConfiguration_AppConfigurationDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(appConfigurationAppService).findById(999L);

        UpdateAppConfigurationInput appConfiguration = new UpdateAppConfigurationInput();
        appConfiguration.setId(999L);
        appConfiguration.setType("999");
        appConfiguration.setValue("999");

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(appConfiguration);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(put("/appConfiguration/999").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Unable to update. AppConfiguration with id=999 not found."));
    }

    @Test
    public void UpdateAppConfiguration_AppConfigurationExists_ReturnStatusOk() throws Exception {
        AppConfigurationEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        entity = appConfiguration_repository.save(entity);
        FindAppConfigurationByIdOutput output = new FindAppConfigurationByIdOutput();
        output.setId(entity.getId());
        output.setType(entity.getType());
        output.setValue(entity.getValue());
        output.setVersiono(entity.getVersiono());

        Mockito.when(appConfigurationAppService.findById(entity.getId())).thenReturn(output);

        UpdateAppConfigurationInput appConfigurationInput = new UpdateAppConfigurationInput();
        appConfigurationInput.setId(entity.getId());
        appConfigurationInput.setType(entity.getType());
        appConfigurationInput.setValue(entity.getValue());

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(appConfigurationInput);

        mvc
            .perform(
                put("/appConfiguration/" + entity.getId() + "/").contentType(MediaType.APPLICATION_JSON).content(json)
            )
            .andExpect(status().isOk());

        AppConfigurationEntity de = createUpdateEntity();
        de.setId(entity.getId());
        appConfiguration_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/appConfiguration?search=id[equals]=1&limit=10&offset=1").contentType(MediaType.APPLICATION_JSON)
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
                            get("/appConfiguration?search=appConfigurationid[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property appConfigurationid not found!"));
    }
}
