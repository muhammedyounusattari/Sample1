package com.sample1.sample1.application.core.authorization.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.authorization.users.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.role.IRoleRepository;
import com.sample1.sample1.domain.core.authorization.users.*;
import com.sample1.sample1.domain.core.authorization.users.QUsersEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.sample1.sample1.domain.core.authorization.userspreference.UserspreferenceEntity;
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
public class UsersAppServiceTest {

    @InjectMocks
    @Spy
    protected UsersAppService _appService;

    @Mock
    protected IUsersRepository _usersRepository;

    @Mock
    protected IUserspreferenceRepository _userspreferenceRepository;

    @Mock
    protected IUsersMapper _mapper;

    @Mock
    protected Logger loggerMock;

    @Mock
    protected LoggingHelper logHelper;

    @Mock
    protected IRoleRepository _roleRepository;

    protected static Long ID = 15L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(_appService);
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());
    }

    @Test
    public void findUsersById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<UsersEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersRepository.findById(anyLong())).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(ID)).isEqualTo(null);
    }

    @Test
    public void findUsersById_IdIsNotNullAndIdExists_ReturnUsers() {
        UsersEntity users = mock(UsersEntity.class);
        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) users);
        UserspreferenceEntity userspreference = new UserspreferenceEntity();
        Optional<UserspreferenceEntity> userspreferenceOptional = Optional.of((UserspreferenceEntity) userspreference);
        Mockito.when(_userspreferenceRepository.findById(ID)).thenReturn(userspreferenceOptional);
        Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);

        Assertions
            .assertThat(_appService.findById(ID))
            .isEqualTo(_mapper.usersEntityToFindUsersByIdOutput(users, userspreference));
    }

    @Test
    public void createUsers_UsersIsNotNullAndUsersDoesNotExist_StoreUsers() {
        UsersEntity usersEntity = mock(UsersEntity.class);
        CreateUsersInput usersInput = new CreateUsersInput();

        UserspreferenceEntity userspreference = new UserspreferenceEntity();
        Optional<UserspreferenceEntity> userspreferenceOptional = Optional.of((UserspreferenceEntity) userspreference);
        Mockito.when(_userspreferenceRepository.findById(ID)).thenReturn(userspreferenceOptional);

        Mockito.when(_mapper.createUsersInputToUsersEntity(any(CreateUsersInput.class))).thenReturn(usersEntity);
        Mockito.when(_usersRepository.save(any(UsersEntity.class))).thenReturn(usersEntity);

        Assertions
            .assertThat(_appService.create(usersInput))
            .isEqualTo(_mapper.usersEntityToCreateUsersOutput(usersEntity, userspreference));
    }

    @Test
    public void updateUsers_UsersIdIsNotNullAndIdExists_ReturnUpdatedUsers() {
        UsersEntity usersEntity = mock(UsersEntity.class);
        UpdateUsersInput users = mock(UpdateUsersInput.class);

        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) usersEntity);
        Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);

        Mockito.when(_mapper.updateUsersInputToUsersEntity(any(UpdateUsersInput.class))).thenReturn(usersEntity);
        Mockito.when(_usersRepository.save(any(UsersEntity.class))).thenReturn(usersEntity);
        Assertions
            .assertThat(_appService.update(ID, users))
            .isEqualTo(_mapper.usersEntityToUpdateUsersOutput(usersEntity));
    }

    @Test
    public void deleteUsers_UsersIsNotNullAndUsersExists_UsersRemoved() {
        UsersEntity users = mock(UsersEntity.class);
        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) users);
        Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
        UserspreferenceEntity userspreference = mock(UserspreferenceEntity.class);
        Optional<UserspreferenceEntity> userspreferenceOptional = Optional.of((UserspreferenceEntity) userspreference);
        Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
        Mockito.when(_userspreferenceRepository.findById(anyLong())).thenReturn(userspreferenceOptional);
        doNothing().when(_userspreferenceRepository).delete(any(UserspreferenceEntity.class));

        _appService.delete(ID);
        verify(_usersRepository).delete(users);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<UsersEntity> list = new ArrayList<>();
        Page<UsersEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsersByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usersRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<UsersEntity> list = new ArrayList<>();
        UsersEntity users = mock(UsersEntity.class);
        list.add(users);
        Page<UsersEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsersByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        UserspreferenceEntity userspreference = new UserspreferenceEntity();
        Optional<UserspreferenceEntity> userspreferenceOptional = Optional.of((UserspreferenceEntity) userspreference);
        Mockito.when(_userspreferenceRepository.findById(any(Long.class))).thenReturn(userspreferenceOptional);

        output.add(_mapper.usersEntityToFindUsersByIdOutput(users, userspreference));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usersRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QUsersEntity users = QUsersEntity.usersEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        map.put("emailaddress", searchFields);
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(users.emailaddress.eq("xyz"));
        Assertions.assertThat(_appService.searchKeyValuePair(users, map, searchMap)).isEqualTo(builder);
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
        list.add("emailaddress");
        list.add("firstname");
        list.add("lastname");
        list.add("password");
        list.add("triggerGroup");
        list.add("triggerName");
        list.add("username");
        _appService.checkProperties(list);
    }

    @Test
    public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
        Map<String, SearchFields> map = new HashMap<>();
        QUsersEntity users = QUsersEntity.usersEntity;
        List<SearchFields> fieldsList = new ArrayList<>();
        SearchFields fields = new SearchFields();
        SearchCriteria search = new SearchCriteria();
        search.setType(3);
        search.setValue("xyz");
        search.setOperator("equals");
        fields.setFieldName("emailaddress");
        fields.setOperator("equals");
        fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(users.emailaddress.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
        Mockito
            .doReturn(builder)
            .when(_appService)
            .searchKeyValuePair(any(QUsersEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    @Test
    public void findUsersByName_NameIsNotNullAndUsersDoesNotExist_ReturnNull() {
        Mockito.when(_usersRepository.findByUsername(anyString())).thenReturn(null);
        Assertions.assertThat(_appService.findByUsername("User1")).isEqualTo(null);
    }

    @Test
    public void findUsersByName_NameIsNotNullAndUsersExists_ReturnAUsers() {
        UsersEntity users = mock(UsersEntity.class);
        Mockito.when(_usersRepository.findByUsername(anyString())).thenReturn(users);
        Assertions
            .assertThat(_appService.findByUsername("User1"))
            .isEqualTo(_mapper.usersEntityToFindUsersByUsernameOutput(users));
    }

    @Test
    public void ParsetimesheetsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("userid", keyString);
        Assertions.assertThat(_appService.parseTimesheetsJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }

    @Test
    public void ParseuserspermissionsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("usersId", keyString);
        Assertions.assertThat(_appService.parseUserspermissionsJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }

    @Test
    public void ParseusersrolesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("usersId", keyString);
        Assertions.assertThat(_appService.parseUsersrolesJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }

    @Test
    public void ParseusertasksJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("userid", keyString);
        Assertions.assertThat(_appService.parseUsertasksJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }
}
