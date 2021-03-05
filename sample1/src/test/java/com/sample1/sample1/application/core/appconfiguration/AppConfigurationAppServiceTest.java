package com.sample1.sample1.application.core.appconfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.appconfiguration.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.appconfiguration.*;
import com.sample1.sample1.domain.core.appconfiguration.AppConfigurationEntity;
import com.sample1.sample1.domain.core.appconfiguration.QAppConfigurationEntity;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class AppConfigurationAppServiceTest {

    @InjectMocks
    @Spy
    protected AppConfigurationAppService _appService;

    @Mock
    protected IAppConfigurationRepository _appConfigurationRepository;

    @Mock
    protected IAppConfigurationMapper _mapper;

    @Mock
    protected Logger loggerMock;

    @Mock
    protected LoggingHelper logHelper;

    protected static Long ID = 15L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(_appService);
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());
    }

    @Test
    public void findAppConfigurationById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<AppConfigurationEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_appConfigurationRepository.findById(anyLong())).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(ID)).isEqualTo(null);
    }

    @Test
    public void findAppConfigurationById_IdIsNotNullAndIdExists_ReturnAppConfiguration() {
        AppConfigurationEntity appConfiguration = mock(AppConfigurationEntity.class);
        Optional<AppConfigurationEntity> appConfigurationOptional = Optional.of(
            (AppConfigurationEntity) appConfiguration
        );
        Mockito.when(_appConfigurationRepository.findById(anyLong())).thenReturn(appConfigurationOptional);

        Assertions
            .assertThat(_appService.findById(ID))
            .isEqualTo(_mapper.appConfigurationEntityToFindAppConfigurationByIdOutput(appConfiguration));
    }

    @Test
    public void createAppConfiguration_AppConfigurationIsNotNullAndAppConfigurationDoesNotExist_StoreAppConfiguration() {
        AppConfigurationEntity appConfigurationEntity = mock(AppConfigurationEntity.class);
        CreateAppConfigurationInput appConfigurationInput = new CreateAppConfigurationInput();

        Mockito
            .when(_mapper.createAppConfigurationInputToAppConfigurationEntity(any(CreateAppConfigurationInput.class)))
            .thenReturn(appConfigurationEntity);
        Mockito
            .when(_appConfigurationRepository.save(any(AppConfigurationEntity.class)))
            .thenReturn(appConfigurationEntity);

        Assertions
            .assertThat(_appService.create(appConfigurationInput))
            .isEqualTo(_mapper.appConfigurationEntityToCreateAppConfigurationOutput(appConfigurationEntity));
    }

    @Test
    public void updateAppConfiguration_AppConfigurationIdIsNotNullAndIdExists_ReturnUpdatedAppConfiguration() {
        AppConfigurationEntity appConfigurationEntity = mock(AppConfigurationEntity.class);
        UpdateAppConfigurationInput appConfiguration = mock(UpdateAppConfigurationInput.class);

        Optional<AppConfigurationEntity> appConfigurationOptional = Optional.of(
            (AppConfigurationEntity) appConfigurationEntity
        );
        Mockito.when(_appConfigurationRepository.findById(anyLong())).thenReturn(appConfigurationOptional);

        Mockito
            .when(_mapper.updateAppConfigurationInputToAppConfigurationEntity(any(UpdateAppConfigurationInput.class)))
            .thenReturn(appConfigurationEntity);
        Mockito
            .when(_appConfigurationRepository.save(any(AppConfigurationEntity.class)))
            .thenReturn(appConfigurationEntity);
        Assertions
            .assertThat(_appService.update(ID, appConfiguration))
            .isEqualTo(_mapper.appConfigurationEntityToUpdateAppConfigurationOutput(appConfigurationEntity));
    }

    @Test
    public void deleteAppConfiguration_AppConfigurationIsNotNullAndAppConfigurationExists_AppConfigurationRemoved() {
        AppConfigurationEntity appConfiguration = mock(AppConfigurationEntity.class);
        Optional<AppConfigurationEntity> appConfigurationOptional = Optional.of(
            (AppConfigurationEntity) appConfiguration
        );
        Mockito.when(_appConfigurationRepository.findById(anyLong())).thenReturn(appConfigurationOptional);

        _appService.delete(ID);
        verify(_appConfigurationRepository).delete(appConfiguration);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<AppConfigurationEntity> list = new ArrayList<>();
        Page<AppConfigurationEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindAppConfigurationByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito
            .when(_appConfigurationRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<AppConfigurationEntity> list = new ArrayList<>();
        AppConfigurationEntity appConfiguration = mock(AppConfigurationEntity.class);
        list.add(appConfiguration);
        Page<AppConfigurationEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindAppConfigurationByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.appConfigurationEntityToFindAppConfigurationByIdOutput(appConfiguration));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito
            .when(_appConfigurationRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QAppConfigurationEntity appConfiguration = QAppConfigurationEntity.appConfigurationEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        map.put("type", searchFields);
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(appConfiguration.type.eq("xyz"));
        Assertions.assertThat(_appService.searchKeyValuePair(appConfiguration, map, searchMap)).isEqualTo(builder);
    }

    @Test(expected = Exception.class)
    public void checkProperties_PropertyDoesNotExist_ThrowException() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("xyz");
        _appService.checkProperties(list);
    }

    @Test
    public void checkProperties_PropertyExists_ReturnNothing() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("type");
        list.add("value");
        _appService.checkProperties(list);
    }

    @Test
    public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
        Map<String, SearchFields> map = new HashMap<>();
        QAppConfigurationEntity appConfiguration = QAppConfigurationEntity.appConfigurationEntity;
        List<SearchFields> fieldsList = new ArrayList<>();
        SearchFields fields = new SearchFields();
        SearchCriteria search = new SearchCriteria();
        search.setType(3);
        search.setValue("xyz");
        search.setOperator("equals");
        fields.setFieldName("type");
        fields.setOperator("equals");
        fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(appConfiguration.type.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
        Mockito
            .doReturn(builder)
            .when(_appService)
            .searchKeyValuePair(any(QAppConfigurationEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }
}
