package com.sample1.sample1.application.core.usertask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.usertask.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.task.ITaskRepository;
import com.sample1.sample1.domain.core.task.TaskEntity;
import com.sample1.sample1.domain.core.usertask.*;
import com.sample1.sample1.domain.core.usertask.QUsertaskEntity;
import com.sample1.sample1.domain.core.usertask.UsertaskEntity;
import com.sample1.sample1.domain.core.usertask.UsertaskId;
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
public class UsertaskAppServiceTest {

    @InjectMocks
    @Spy
    protected UsertaskAppService _appService;

    @Mock
    protected IUsertaskRepository _usertaskRepository;

    @Mock
    protected ITaskRepository _taskRepository;

    @Mock
    protected IUsersRepository _usersRepository;

    @Mock
    protected IUsertaskMapper _mapper;

    @Mock
    protected Logger loggerMock;

    @Mock
    protected LoggingHelper logHelper;

    @Mock
    protected UsertaskId usertaskId;

    private static final Long ID = 15L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(_appService);
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());
    }

    @Test
    public void findUsertaskById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<UsertaskEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(usertaskId)).isEqualTo(null);
    }

    @Test
    public void findUsertaskById_IdIsNotNullAndIdExists_ReturnUsertask() {
        UsertaskEntity usertask = mock(UsertaskEntity.class);
        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertask);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);

        Assertions
            .assertThat(_appService.findById(usertaskId))
            .isEqualTo(_mapper.usertaskEntityToFindUsertaskByIdOutput(usertask));
    }

    @Test
    public void createUsertask_UsertaskIsNotNullAndUsertaskDoesNotExist_StoreUsertask() {
        UsertaskEntity usertaskEntity = mock(UsertaskEntity.class);
        CreateUsertaskInput usertaskInput = new CreateUsertaskInput();

        TaskEntity task = mock(TaskEntity.class);
        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) task);
        usertaskInput.setTaskid(15L);

        Mockito.when(_taskRepository.findById(any(Long.class))).thenReturn(taskOptional);

        UsersEntity users = mock(UsersEntity.class);
        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) users);
        usertaskInput.setUserid(15L);

        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);

        Mockito
            .when(_mapper.createUsertaskInputToUsertaskEntity(any(CreateUsertaskInput.class)))
            .thenReturn(usertaskEntity);
        Mockito.when(_usertaskRepository.save(any(UsertaskEntity.class))).thenReturn(usertaskEntity);

        Assertions
            .assertThat(_appService.create(usertaskInput))
            .isEqualTo(_mapper.usertaskEntityToCreateUsertaskOutput(usertaskEntity));
    }

    @Test
    public void createUsertask_UsertaskIsNotNullAndUsertaskDoesNotExistAndChildIsNullAndChildIsNotMandatory_StoreUsertask() {
        UsertaskEntity usertaskEntity = mock(UsertaskEntity.class);
        CreateUsertaskInput usertask = mock(CreateUsertaskInput.class);

        Mockito
            .when(_mapper.createUsertaskInputToUsertaskEntity(any(CreateUsertaskInput.class)))
            .thenReturn(usertaskEntity);
        Mockito.when(_usertaskRepository.save(any(UsertaskEntity.class))).thenReturn(usertaskEntity);
        Assertions
            .assertThat(_appService.create(usertask))
            .isEqualTo(_mapper.usertaskEntityToCreateUsertaskOutput(usertaskEntity));
    }

    @Test
    public void updateUsertask_UsertaskIsNotNullAndUsertaskDoesNotExistAndChildIsNullAndChildIsNotMandatory_ReturnUpdatedUsertask() {
        UsertaskEntity usertaskEntity = mock(UsertaskEntity.class);
        UpdateUsertaskInput usertask = mock(UpdateUsertaskInput.class);

        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertaskEntity);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);

        Mockito
            .when(_mapper.updateUsertaskInputToUsertaskEntity(any(UpdateUsertaskInput.class)))
            .thenReturn(usertaskEntity);
        Mockito.when(_usertaskRepository.save(any(UsertaskEntity.class))).thenReturn(usertaskEntity);
        Assertions
            .assertThat(_appService.update(usertaskId, usertask))
            .isEqualTo(_mapper.usertaskEntityToUpdateUsertaskOutput(usertaskEntity));
    }

    @Test
    public void updateUsertask_UsertaskIdIsNotNullAndIdExists_ReturnUpdatedUsertask() {
        UsertaskEntity usertaskEntity = mock(UsertaskEntity.class);
        UpdateUsertaskInput usertask = mock(UpdateUsertaskInput.class);

        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertaskEntity);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);

        Mockito
            .when(_mapper.updateUsertaskInputToUsertaskEntity(any(UpdateUsertaskInput.class)))
            .thenReturn(usertaskEntity);
        Mockito.when(_usertaskRepository.save(any(UsertaskEntity.class))).thenReturn(usertaskEntity);
        Assertions
            .assertThat(_appService.update(usertaskId, usertask))
            .isEqualTo(_mapper.usertaskEntityToUpdateUsertaskOutput(usertaskEntity));
    }

    @Test
    public void deleteUsertask_UsertaskIsNotNullAndUsertaskExists_UsertaskRemoved() {
        UsertaskEntity usertask = mock(UsertaskEntity.class);
        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertask);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);

        _appService.delete(usertaskId);
        verify(_usertaskRepository).delete(usertask);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<UsertaskEntity> list = new ArrayList<>();
        Page<UsertaskEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsertaskByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usertaskRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<UsertaskEntity> list = new ArrayList<>();
        UsertaskEntity usertask = mock(UsertaskEntity.class);
        list.add(usertask);
        Page<UsertaskEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsertaskByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.usertaskEntityToFindUsertaskByIdOutput(usertask));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usertaskRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QUsertaskEntity usertask = QUsertaskEntity.usertaskEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        Assertions.assertThat(_appService.searchKeyValuePair(usertask, map, searchMap)).isEqualTo(builder);
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
        _appService.checkProperties(list);
    }

    @Test
    public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
        Map<String, SearchFields> map = new HashMap<>();
        QUsertaskEntity usertask = QUsertaskEntity.usertaskEntity;
        List<SearchFields> fieldsList = new ArrayList<>();
        SearchFields fields = new SearchFields();
        SearchCriteria search = new SearchCriteria();
        search.setType(3);
        search.setValue("xyz");
        search.setOperator("equals");
        fields.setOperator("equals");
        fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
        BooleanBuilder builder = new BooleanBuilder();
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
        Mockito
            .doReturn(builder)
            .when(_appService)
            .searchKeyValuePair(any(QUsertaskEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    //Task
    @Test
    public void GetTask_IfUsertaskIdAndTaskIdIsNotNullAndUsertaskExists_ReturnTask() {
        UsertaskEntity usertask = mock(UsertaskEntity.class);
        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertask);
        TaskEntity taskEntity = mock(TaskEntity.class);

        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);
        Mockito.when(usertask.getTask()).thenReturn(taskEntity);
        Assertions
            .assertThat(_appService.getTask(usertaskId))
            .isEqualTo(_mapper.taskEntityToGetTaskOutput(taskEntity, usertask));
    }

    @Test
    public void GetTask_IfUsertaskIdAndTaskIdIsNotNullAndUsertaskDoesNotExist_ReturnNull() {
        Optional<UsertaskEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getTask(usertaskId)).isEqualTo(null);
    }

    //Users
    @Test
    public void GetUsers_IfUsertaskIdAndUsersIdIsNotNullAndUsertaskExists_ReturnUsers() {
        UsertaskEntity usertask = mock(UsertaskEntity.class);
        Optional<UsertaskEntity> usertaskOptional = Optional.of((UsertaskEntity) usertask);
        UsersEntity usersEntity = mock(UsersEntity.class);

        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(usertaskOptional);
        Mockito.when(usertask.getUsers()).thenReturn(usersEntity);
        Assertions
            .assertThat(_appService.getUsers(usertaskId))
            .isEqualTo(_mapper.usersEntityToGetUsersOutput(usersEntity, usertask));
    }

    @Test
    public void GetUsers_IfUsertaskIdAndUsersIdIsNotNullAndUsertaskDoesNotExist_ReturnNull() {
        Optional<UsertaskEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usertaskRepository.findById(any(UsertaskId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getUsers(usertaskId)).isEqualTo(null);
    }

    @Test
    public void ParseUsertaskKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnUsertaskId() {
        String keyString = "taskid=15,userid=15";

        UsertaskId usertaskId = new UsertaskId();
        usertaskId.setTaskid(15L);
        usertaskId.setUserid(15L);

        Assertions.assertThat(_appService.parseUsertaskKey(keyString)).isEqualToComparingFieldByField(usertaskId);
    }

    @Test
    public void ParseUsertaskKey_KeysStringIsEmpty_ReturnNull() {
        String keyString = "";
        Assertions.assertThat(_appService.parseUsertaskKey(keyString)).isEqualTo(null);
    }

    @Test
    public void ParseUsertaskKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        String keyString = "taskid";

        Assertions.assertThat(_appService.parseUsertaskKey(keyString)).isEqualTo(null);
    }
}
