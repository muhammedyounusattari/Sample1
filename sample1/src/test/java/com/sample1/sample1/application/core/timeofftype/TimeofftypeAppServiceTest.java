package com.sample1.sample1.application.core.timeofftype;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.timeofftype.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.timeofftype.*;
import com.sample1.sample1.domain.core.timeofftype.QTimeofftypeEntity;
import com.sample1.sample1.domain.core.timeofftype.TimeofftypeEntity;
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
public class TimeofftypeAppServiceTest {

    @InjectMocks
    @Spy
    protected TimeofftypeAppService _appService;

    @Mock
    protected ITimeofftypeRepository _timeofftypeRepository;

    @Mock
    protected ITimeofftypeMapper _mapper;

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
    public void findTimeofftypeById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<TimeofftypeEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_timeofftypeRepository.findById(anyLong())).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(ID)).isEqualTo(null);
    }

    @Test
    public void findTimeofftypeById_IdIsNotNullAndIdExists_ReturnTimeofftype() {
        TimeofftypeEntity timeofftype = mock(TimeofftypeEntity.class);
        Optional<TimeofftypeEntity> timeofftypeOptional = Optional.of((TimeofftypeEntity) timeofftype);
        Mockito.when(_timeofftypeRepository.findById(anyLong())).thenReturn(timeofftypeOptional);

        Assertions
            .assertThat(_appService.findById(ID))
            .isEqualTo(_mapper.timeofftypeEntityToFindTimeofftypeByIdOutput(timeofftype));
    }

    @Test
    public void createTimeofftype_TimeofftypeIsNotNullAndTimeofftypeDoesNotExist_StoreTimeofftype() {
        TimeofftypeEntity timeofftypeEntity = mock(TimeofftypeEntity.class);
        CreateTimeofftypeInput timeofftypeInput = new CreateTimeofftypeInput();

        Mockito
            .when(_mapper.createTimeofftypeInputToTimeofftypeEntity(any(CreateTimeofftypeInput.class)))
            .thenReturn(timeofftypeEntity);
        Mockito.when(_timeofftypeRepository.save(any(TimeofftypeEntity.class))).thenReturn(timeofftypeEntity);

        Assertions
            .assertThat(_appService.create(timeofftypeInput))
            .isEqualTo(_mapper.timeofftypeEntityToCreateTimeofftypeOutput(timeofftypeEntity));
    }

    @Test
    public void updateTimeofftype_TimeofftypeIdIsNotNullAndIdExists_ReturnUpdatedTimeofftype() {
        TimeofftypeEntity timeofftypeEntity = mock(TimeofftypeEntity.class);
        UpdateTimeofftypeInput timeofftype = mock(UpdateTimeofftypeInput.class);

        Optional<TimeofftypeEntity> timeofftypeOptional = Optional.of((TimeofftypeEntity) timeofftypeEntity);
        Mockito.when(_timeofftypeRepository.findById(anyLong())).thenReturn(timeofftypeOptional);

        Mockito
            .when(_mapper.updateTimeofftypeInputToTimeofftypeEntity(any(UpdateTimeofftypeInput.class)))
            .thenReturn(timeofftypeEntity);
        Mockito.when(_timeofftypeRepository.save(any(TimeofftypeEntity.class))).thenReturn(timeofftypeEntity);
        Assertions
            .assertThat(_appService.update(ID, timeofftype))
            .isEqualTo(_mapper.timeofftypeEntityToUpdateTimeofftypeOutput(timeofftypeEntity));
    }

    @Test
    public void deleteTimeofftype_TimeofftypeIsNotNullAndTimeofftypeExists_TimeofftypeRemoved() {
        TimeofftypeEntity timeofftype = mock(TimeofftypeEntity.class);
        Optional<TimeofftypeEntity> timeofftypeOptional = Optional.of((TimeofftypeEntity) timeofftype);
        Mockito.when(_timeofftypeRepository.findById(anyLong())).thenReturn(timeofftypeOptional);

        _appService.delete(ID);
        verify(_timeofftypeRepository).delete(timeofftype);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<TimeofftypeEntity> list = new ArrayList<>();
        Page<TimeofftypeEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindTimeofftypeByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_timeofftypeRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<TimeofftypeEntity> list = new ArrayList<>();
        TimeofftypeEntity timeofftype = mock(TimeofftypeEntity.class);
        list.add(timeofftype);
        Page<TimeofftypeEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindTimeofftypeByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.timeofftypeEntityToFindTimeofftypeByIdOutput(timeofftype));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_timeofftypeRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QTimeofftypeEntity timeofftype = QTimeofftypeEntity.timeofftypeEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        map.put("typename", searchFields);
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(timeofftype.typename.eq("xyz"));
        Assertions.assertThat(_appService.searchKeyValuePair(timeofftype, map, searchMap)).isEqualTo(builder);
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
        list.add("typename");
        _appService.checkProperties(list);
    }

    @Test
    public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
        Map<String, SearchFields> map = new HashMap<>();
        QTimeofftypeEntity timeofftype = QTimeofftypeEntity.timeofftypeEntity;
        List<SearchFields> fieldsList = new ArrayList<>();
        SearchFields fields = new SearchFields();
        SearchCriteria search = new SearchCriteria();
        search.setType(3);
        search.setValue("xyz");
        search.setOperator("equals");
        fields.setFieldName("typename");
        fields.setOperator("equals");
        fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(timeofftype.typename.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
        Mockito
            .doReturn(builder)
            .when(_appService)
            .searchKeyValuePair(any(QTimeofftypeEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    @Test
    public void ParsetimesheetdetailsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("timeofftypeid", keyString);
        Assertions.assertThat(_appService.parseTimesheetdetailsJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }
}
