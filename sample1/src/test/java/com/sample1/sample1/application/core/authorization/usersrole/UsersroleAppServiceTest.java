package com.sample1.sample1.application.core.authorization.usersrole;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.authorization.usersrole.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.role.IRoleRepository;
import com.sample1.sample1.domain.core.authorization.role.RoleEntity;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.usersrole.*;
import com.sample1.sample1.domain.core.authorization.usersrole.QUsersroleEntity;
import com.sample1.sample1.domain.core.authorization.usersrole.UsersroleEntity;
import com.sample1.sample1.domain.core.authorization.usersrole.UsersroleId;
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
public class UsersroleAppServiceTest {

    @InjectMocks
    @Spy
    protected UsersroleAppService _appService;

    @Mock
    protected IUsersroleRepository _usersroleRepository;

    @Mock
    protected IRoleRepository _roleRepository;

    @Mock
    protected IUsersRepository _usersRepository;

    @Mock
    protected IUsersroleMapper _mapper;

    @Mock
    protected Logger loggerMock;

    @Mock
    protected LoggingHelper logHelper;

    @Mock
    protected UsersroleId usersroleId;

    private static final Long ID = 15L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(_appService);
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());
    }

    @Test
    public void findUsersroleById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<UsersroleEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(usersroleId)).isEqualTo(null);
    }

    @Test
    public void findUsersroleById_IdIsNotNullAndIdExists_ReturnUsersrole() {
        UsersroleEntity usersrole = mock(UsersroleEntity.class);
        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersrole);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

        Assertions
            .assertThat(_appService.findById(usersroleId))
            .isEqualTo(_mapper.usersroleEntityToFindUsersroleByIdOutput(usersrole));
    }

    @Test
    public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExist_StoreUsersrole() {
        UsersroleEntity usersroleEntity = mock(UsersroleEntity.class);
        CreateUsersroleInput usersroleInput = new CreateUsersroleInput();

        RoleEntity role = mock(RoleEntity.class);
        Optional<RoleEntity> roleOptional = Optional.of((RoleEntity) role);
        usersroleInput.setRoleId(15L);

        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(roleOptional);

        UsersEntity users = mock(UsersEntity.class);
        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) users);
        usersroleInput.setUsersId(15L);

        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);

        Mockito
            .when(_mapper.createUsersroleInputToUsersroleEntity(any(CreateUsersroleInput.class)))
            .thenReturn(usersroleEntity);
        Mockito.when(_usersroleRepository.save(any(UsersroleEntity.class))).thenReturn(usersroleEntity);

        Assertions
            .assertThat(_appService.create(usersroleInput))
            .isEqualTo(_mapper.usersroleEntityToCreateUsersroleOutput(usersroleEntity));
    }

    @Test
    public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {
        CreateUsersroleInput usersrole = mock(CreateUsersroleInput.class);

        Mockito.when(_mapper.createUsersroleInputToUsersroleEntity(any(CreateUsersroleInput.class))).thenReturn(null);
        Assertions.assertThat(_appService.create(usersrole)).isEqualTo(null);
    }

    @Test
    public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
        CreateUsersroleInput usersrole = new CreateUsersroleInput();

        usersrole.setRoleId(15L);

        Optional<RoleEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.create(usersrole)).isEqualTo(null);
    }

    @Test
    public void updateUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {
        UpdateUsersroleInput usersrole = mock(UpdateUsersroleInput.class);
        UsersroleEntity usersroleEntity = mock(UsersroleEntity.class);

        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersroleEntity);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

        Mockito
            .when(_mapper.updateUsersroleInputToUsersroleEntity(any(UpdateUsersroleInput.class)))
            .thenReturn(usersroleEntity);
        Assertions.assertThat(_appService.update(usersroleId, usersrole)).isEqualTo(null);
    }

    @Test
    public void updateUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
        UpdateUsersroleInput usersrole = new UpdateUsersroleInput();
        usersrole.setRoleId(15L);

        UsersroleEntity usersroleEntity = mock(UsersroleEntity.class);

        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersroleEntity);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

        Mockito
            .when(_mapper.updateUsersroleInputToUsersroleEntity(any(UpdateUsersroleInput.class)))
            .thenReturn(usersroleEntity);
        Optional<RoleEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.update(usersroleId, usersrole)).isEqualTo(null);
    }

    @Test
    public void updateUsersrole_UsersroleIdIsNotNullAndIdExists_ReturnUpdatedUsersrole() {
        UsersroleEntity usersroleEntity = mock(UsersroleEntity.class);
        UpdateUsersroleInput usersrole = mock(UpdateUsersroleInput.class);

        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersroleEntity);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

        Mockito
            .when(_mapper.updateUsersroleInputToUsersroleEntity(any(UpdateUsersroleInput.class)))
            .thenReturn(usersroleEntity);
        Mockito.when(_usersroleRepository.save(any(UsersroleEntity.class))).thenReturn(usersroleEntity);
        Assertions
            .assertThat(_appService.update(usersroleId, usersrole))
            .isEqualTo(_mapper.usersroleEntityToUpdateUsersroleOutput(usersroleEntity));
    }

    @Test
    public void deleteUsersrole_UsersroleIsNotNullAndUsersroleExists_UsersroleRemoved() {
        UsersroleEntity usersrole = mock(UsersroleEntity.class);
        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersrole);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

        _appService.delete(usersroleId);
        verify(_usersroleRepository).delete(usersrole);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<UsersroleEntity> list = new ArrayList<>();
        Page<UsersroleEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsersroleByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usersroleRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<UsersroleEntity> list = new ArrayList<>();
        UsersroleEntity usersrole = mock(UsersroleEntity.class);
        list.add(usersrole);
        Page<UsersroleEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUsersroleByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.usersroleEntityToFindUsersroleByIdOutput(usersrole));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_usersroleRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QUsersroleEntity usersrole = QUsersroleEntity.usersroleEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        Assertions.assertThat(_appService.searchKeyValuePair(usersrole, map, searchMap)).isEqualTo(builder);
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
        QUsersroleEntity usersrole = QUsersroleEntity.usersroleEntity;
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
            .searchKeyValuePair(any(QUsersroleEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    //Role
    @Test
    public void GetRole_IfUsersroleIdAndRoleIdIsNotNullAndUsersroleExists_ReturnRole() {
        UsersroleEntity usersrole = mock(UsersroleEntity.class);
        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersrole);
        RoleEntity roleEntity = mock(RoleEntity.class);

        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
        Mockito.when(usersrole.getRole()).thenReturn(roleEntity);
        Assertions
            .assertThat(_appService.getRole(usersroleId))
            .isEqualTo(_mapper.roleEntityToGetRoleOutput(roleEntity, usersrole));
    }

    @Test
    public void GetRole_IfUsersroleIdAndRoleIdIsNotNullAndUsersroleDoesNotExist_ReturnNull() {
        Optional<UsersroleEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getRole(usersroleId)).isEqualTo(null);
    }

    //Users
    @Test
    public void GetUsers_IfUsersroleIdAndUsersIdIsNotNullAndUsersroleExists_ReturnUsers() {
        UsersroleEntity usersrole = mock(UsersroleEntity.class);
        Optional<UsersroleEntity> usersroleOptional = Optional.of((UsersroleEntity) usersrole);
        UsersEntity usersEntity = mock(UsersEntity.class);

        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
        Mockito.when(usersrole.getUsers()).thenReturn(usersEntity);
        Assertions
            .assertThat(_appService.getUsers(usersroleId))
            .isEqualTo(_mapper.usersEntityToGetUsersOutput(usersEntity, usersrole));
    }

    @Test
    public void GetUsers_IfUsersroleIdAndUsersIdIsNotNullAndUsersroleDoesNotExist_ReturnNull() {
        Optional<UsersroleEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getUsers(usersroleId)).isEqualTo(null);
    }

    @Test
    public void ParseUsersroleKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnUsersroleId() {
        String keyString = "roleId=15,usersId=15";

        UsersroleId usersroleId = new UsersroleId();
        usersroleId.setRoleId(15L);
        usersroleId.setUsersId(15L);

        Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualToComparingFieldByField(usersroleId);
    }

    @Test
    public void ParseUsersroleKey_KeysStringIsEmpty_ReturnNull() {
        String keyString = "";
        Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualTo(null);
    }

    @Test
    public void ParseUsersroleKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        String keyString = "roleId";

        Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualTo(null);
    }
}
