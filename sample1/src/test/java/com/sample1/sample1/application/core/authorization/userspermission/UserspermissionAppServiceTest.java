package com.sample1.sample1.application.core.authorization.userspermission;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.authorization.userspermission.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.permission.IPermissionRepository;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.*;
import com.sample1.sample1.domain.core.authorization.userspermission.QUserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionId;
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
public class UserspermissionAppServiceTest {

    @InjectMocks
    @Spy
    protected UserspermissionAppService _appService;

    @Mock
    protected IUserspermissionRepository _userspermissionRepository;

    @Mock
    protected IPermissionRepository _permissionRepository;

    @Mock
    protected IUsersRepository _usersRepository;

    @Mock
    protected IUserspermissionMapper _mapper;

    @Mock
    protected Logger loggerMock;

    @Mock
    protected LoggingHelper logHelper;

    @Mock
    protected UserspermissionId userspermissionId;

    private static final Long ID = 15L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(_appService);
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());
    }

    @Test
    public void findUserspermissionById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<UserspermissionEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(userspermissionId)).isEqualTo(null);
    }

    @Test
    public void findUserspermissionById_IdIsNotNullAndIdExists_ReturnUserspermission() {
        UserspermissionEntity userspermission = mock(UserspermissionEntity.class);
        Optional<UserspermissionEntity> userspermissionOptional = Optional.of((UserspermissionEntity) userspermission);
        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);

        Assertions
            .assertThat(_appService.findById(userspermissionId))
            .isEqualTo(_mapper.userspermissionEntityToFindUserspermissionByIdOutput(userspermission));
    }

    @Test
    public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExist_StoreUserspermission() {
        UserspermissionEntity userspermissionEntity = mock(UserspermissionEntity.class);
        CreateUserspermissionInput userspermissionInput = new CreateUserspermissionInput();

        PermissionEntity permission = mock(PermissionEntity.class);
        Optional<PermissionEntity> permissionOptional = Optional.of((PermissionEntity) permission);
        userspermissionInput.setPermissionId(15L);

        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(permissionOptional);

        UsersEntity users = mock(UsersEntity.class);
        Optional<UsersEntity> usersOptional = Optional.of((UsersEntity) users);
        userspermissionInput.setUsersId(15L);

        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);

        Mockito
            .when(_mapper.createUserspermissionInputToUserspermissionEntity(any(CreateUserspermissionInput.class)))
            .thenReturn(userspermissionEntity);
        Mockito
            .when(_userspermissionRepository.save(any(UserspermissionEntity.class)))
            .thenReturn(userspermissionEntity);

        CreateUserspermissionOutput output = new CreateUserspermissionOutput();
        Mockito
            .when(_mapper.userspermissionEntityToCreateUserspermissionOutput(any(UserspermissionEntity.class)))
            .thenReturn(output);

        Assertions
            .assertThat(_appService.create(userspermissionInput))
            .isEqualTo(_mapper.userspermissionEntityToCreateUserspermissionOutput(userspermissionEntity));
    }

    @Test
    public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {
        CreateUserspermissionInput userspermission = mock(CreateUserspermissionInput.class);

        Mockito
            .when(_mapper.createUserspermissionInputToUserspermissionEntity(any(CreateUserspermissionInput.class)))
            .thenReturn(null);
        Assertions.assertThat(_appService.create(userspermission)).isEqualTo(null);
    }

    @Test
    public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
        CreateUserspermissionInput userspermission = new CreateUserspermissionInput();

        userspermission.setPermissionId(15L);

        Optional<PermissionEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.create(userspermission)).isEqualTo(null);
    }

    @Test
    public void updateUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {
        UpdateUserspermissionInput userspermission = mock(UpdateUserspermissionInput.class);
        UserspermissionEntity userspermissionEntity = mock(UserspermissionEntity.class);

        Optional<UserspermissionEntity> userspermissionOptional = Optional.of(
            (UserspermissionEntity) userspermissionEntity
        );
        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);

        Mockito
            .when(_mapper.updateUserspermissionInputToUserspermissionEntity(any(UpdateUserspermissionInput.class)))
            .thenReturn(userspermissionEntity);
        Assertions.assertThat(_appService.update(userspermissionId, userspermission)).isEqualTo(null);
    }

    @Test
    public void updateUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
        UpdateUserspermissionInput userspermission = new UpdateUserspermissionInput();
        userspermission.setPermissionId(15L);

        UserspermissionEntity userspermissionEntity = mock(UserspermissionEntity.class);

        Optional<UserspermissionEntity> userspermissionOptional = Optional.of(
            (UserspermissionEntity) userspermissionEntity
        );
        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);

        Mockito
            .when(_mapper.updateUserspermissionInputToUserspermissionEntity(any(UpdateUserspermissionInput.class)))
            .thenReturn(userspermissionEntity);
        Optional<PermissionEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.update(userspermissionId, userspermission)).isEqualTo(null);
    }

    @Test
    public void updateUserspermission_UserspermissionIdIsNotNullAndIdExists_ReturnUpdatedUserspermission() {
        UserspermissionEntity userspermissionEntity = mock(UserspermissionEntity.class);
        UpdateUserspermissionInput userspermission = mock(UpdateUserspermissionInput.class);

        Optional<UserspermissionEntity> userspermissionOptional = Optional.of(
            (UserspermissionEntity) userspermissionEntity
        );
        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);

        Mockito
            .when(_mapper.updateUserspermissionInputToUserspermissionEntity(any(UpdateUserspermissionInput.class)))
            .thenReturn(userspermissionEntity);
        Mockito
            .when(_userspermissionRepository.save(any(UserspermissionEntity.class)))
            .thenReturn(userspermissionEntity);
        Assertions
            .assertThat(_appService.update(userspermissionId, userspermission))
            .isEqualTo(_mapper.userspermissionEntityToUpdateUserspermissionOutput(userspermissionEntity));
    }

    @Test
    public void deleteUserspermission_UserspermissionIsNotNullAndUserspermissionExists_UserspermissionRemoved() {
        UserspermissionEntity userspermission = mock(UserspermissionEntity.class);
        Optional<UserspermissionEntity> userspermissionOptional = Optional.of((UserspermissionEntity) userspermission);
        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);

        _appService.delete(userspermissionId);
        verify(_userspermissionRepository).delete(userspermission);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<UserspermissionEntity> list = new ArrayList<>();
        Page<UserspermissionEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUserspermissionByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito
            .when(_userspermissionRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<UserspermissionEntity> list = new ArrayList<>();
        UserspermissionEntity userspermission = mock(UserspermissionEntity.class);
        list.add(userspermission);
        Page<UserspermissionEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindUserspermissionByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.userspermissionEntityToFindUserspermissionByIdOutput(userspermission));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito
            .when(_userspermissionRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QUserspermissionEntity userspermission = QUserspermissionEntity.userspermissionEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        Assertions.assertThat(_appService.searchKeyValuePair(userspermission, map, searchMap)).isEqualTo(builder);
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
        QUserspermissionEntity userspermission = QUserspermissionEntity.userspermissionEntity;
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
            .searchKeyValuePair(any(QUserspermissionEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    //Permission
    @Test
    public void GetPermission_IfUserspermissionIdAndPermissionIdIsNotNullAndUserspermissionExists_ReturnPermission() {
        UserspermissionEntity userspermission = mock(UserspermissionEntity.class);
        Optional<UserspermissionEntity> userspermissionOptional = Optional.of((UserspermissionEntity) userspermission);
        PermissionEntity permissionEntity = mock(PermissionEntity.class);

        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);
        Mockito.when(userspermission.getPermission()).thenReturn(permissionEntity);
        Assertions
            .assertThat(_appService.getPermission(userspermissionId))
            .isEqualTo(_mapper.permissionEntityToGetPermissionOutput(permissionEntity, userspermission));
    }

    @Test
    public void GetPermission_IfUserspermissionIdAndPermissionIdIsNotNullAndUserspermissionDoesNotExist_ReturnNull() {
        Optional<UserspermissionEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getPermission(userspermissionId)).isEqualTo(null);
    }

    //Users
    @Test
    public void GetUsers_IfUserspermissionIdAndUsersIdIsNotNullAndUserspermissionExists_ReturnUsers() {
        UserspermissionEntity userspermission = mock(UserspermissionEntity.class);
        Optional<UserspermissionEntity> userspermissionOptional = Optional.of((UserspermissionEntity) userspermission);
        UsersEntity usersEntity = mock(UsersEntity.class);

        Mockito
            .when(_userspermissionRepository.findById(any(UserspermissionId.class)))
            .thenReturn(userspermissionOptional);
        Mockito.when(userspermission.getUsers()).thenReturn(usersEntity);
        Assertions
            .assertThat(_appService.getUsers(userspermissionId))
            .isEqualTo(_mapper.usersEntityToGetUsersOutput(usersEntity, userspermission));
    }

    @Test
    public void GetUsers_IfUserspermissionIdAndUsersIdIsNotNullAndUserspermissionDoesNotExist_ReturnNull() {
        Optional<UserspermissionEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getUsers(userspermissionId)).isEqualTo(null);
    }

    @Test
    public void ParseUserspermissionKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnUserspermissionId() {
        String keyString = "permissionId=15,usersId=15";

        UserspermissionId userspermissionId = new UserspermissionId();
        userspermissionId.setPermissionId(15L);
        userspermissionId.setUsersId(15L);

        Assertions
            .assertThat(_appService.parseUserspermissionKey(keyString))
            .isEqualToComparingFieldByField(userspermissionId);
    }

    @Test
    public void ParseUserspermissionKey_KeysStringIsEmpty_ReturnNull() {
        String keyString = "";
        Assertions.assertThat(_appService.parseUserspermissionKey(keyString)).isEqualTo(null);
    }

    @Test
    public void ParseUserspermissionKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        String keyString = "permissionId";

        Assertions.assertThat(_appService.parseUserspermissionKey(keyString)).isEqualTo(null);
    }
}
