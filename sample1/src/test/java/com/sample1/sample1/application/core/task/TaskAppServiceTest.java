package com.sample1.sample1.application.core.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sample1.sample1.application.core.task.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.project.IProjectRepository;
import com.sample1.sample1.domain.core.project.ProjectEntity;
import com.sample1.sample1.domain.core.task.*;
import com.sample1.sample1.domain.core.task.QTaskEntity;
import com.sample1.sample1.domain.core.task.TaskEntity;
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
public class TaskAppServiceTest {

    @InjectMocks
    @Spy
    protected TaskAppService _appService;

    @Mock
    protected ITaskRepository _taskRepository;

    @Mock
    protected IProjectRepository _projectRepository;

    @Mock
    protected ITaskMapper _mapper;

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
    public void findTaskById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
        Optional<TaskEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(nullOptional);
        Assertions.assertThat(_appService.findById(ID)).isEqualTo(null);
    }

    @Test
    public void findTaskById_IdIsNotNullAndIdExists_ReturnTask() {
        TaskEntity task = mock(TaskEntity.class);
        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) task);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(taskOptional);

        Assertions.assertThat(_appService.findById(ID)).isEqualTo(_mapper.taskEntityToFindTaskByIdOutput(task));
    }

    @Test
    public void createTask_TaskIsNotNullAndTaskDoesNotExist_StoreTask() {
        TaskEntity taskEntity = mock(TaskEntity.class);
        CreateTaskInput taskInput = new CreateTaskInput();

        ProjectEntity project = mock(ProjectEntity.class);
        Optional<ProjectEntity> projectOptional = Optional.of((ProjectEntity) project);
        taskInput.setProjectid(15L);

        Mockito.when(_projectRepository.findById(any(Long.class))).thenReturn(projectOptional);

        Mockito.when(_mapper.createTaskInputToTaskEntity(any(CreateTaskInput.class))).thenReturn(taskEntity);
        Mockito.when(_taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        Assertions
            .assertThat(_appService.create(taskInput))
            .isEqualTo(_mapper.taskEntityToCreateTaskOutput(taskEntity));
    }

    @Test
    public void createTask_TaskIsNotNullAndTaskDoesNotExistAndChildIsNullAndChildIsNotMandatory_StoreTask() {
        TaskEntity taskEntity = mock(TaskEntity.class);
        CreateTaskInput task = mock(CreateTaskInput.class);

        Mockito.when(_mapper.createTaskInputToTaskEntity(any(CreateTaskInput.class))).thenReturn(taskEntity);
        Mockito.when(_taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        Assertions.assertThat(_appService.create(task)).isEqualTo(_mapper.taskEntityToCreateTaskOutput(taskEntity));
    }

    @Test
    public void updateTask_TaskIsNotNullAndTaskDoesNotExistAndChildIsNullAndChildIsNotMandatory_ReturnUpdatedTask() {
        TaskEntity taskEntity = mock(TaskEntity.class);
        UpdateTaskInput task = mock(UpdateTaskInput.class);

        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) taskEntity);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(taskOptional);

        Mockito.when(_mapper.updateTaskInputToTaskEntity(any(UpdateTaskInput.class))).thenReturn(taskEntity);
        Mockito.when(_taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        Assertions.assertThat(_appService.update(ID, task)).isEqualTo(_mapper.taskEntityToUpdateTaskOutput(taskEntity));
    }

    @Test
    public void updateTask_TaskIdIsNotNullAndIdExists_ReturnUpdatedTask() {
        TaskEntity taskEntity = mock(TaskEntity.class);
        UpdateTaskInput task = mock(UpdateTaskInput.class);

        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) taskEntity);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(taskOptional);

        Mockito.when(_mapper.updateTaskInputToTaskEntity(any(UpdateTaskInput.class))).thenReturn(taskEntity);
        Mockito.when(_taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        Assertions.assertThat(_appService.update(ID, task)).isEqualTo(_mapper.taskEntityToUpdateTaskOutput(taskEntity));
    }

    @Test
    public void deleteTask_TaskIsNotNullAndTaskExists_TaskRemoved() {
        TaskEntity task = mock(TaskEntity.class);
        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) task);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(taskOptional);

        _appService.delete(ID);
        verify(_taskRepository).delete(task);
    }

    @Test
    public void find_ListIsEmpty_ReturnList() throws Exception {
        List<TaskEntity> list = new ArrayList<>();
        Page<TaskEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindTaskByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_taskRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void find_ListIsNotEmpty_ReturnList() throws Exception {
        List<TaskEntity> list = new ArrayList<>();
        TaskEntity task = mock(TaskEntity.class);
        list.add(task);
        Page<TaskEntity> foundPage = new PageImpl(list);
        Pageable pageable = mock(Pageable.class);
        List<FindTaskByIdOutput> output = new ArrayList<>();
        SearchCriteria search = new SearchCriteria();

        output.add(_mapper.taskEntityToFindTaskByIdOutput(task));

        Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
        Mockito.when(_taskRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);
        Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
    }

    @Test
    public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
        QTaskEntity task = QTaskEntity.taskEntity;
        SearchFields searchFields = new SearchFields();
        searchFields.setOperator("equals");
        searchFields.setSearchValue("xyz");
        Map<String, SearchFields> map = new HashMap<>();
        map.put("description", searchFields);
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("xyz", String.valueOf(ID));
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(task.description.eq("xyz"));
        Assertions.assertThat(_appService.searchKeyValuePair(task, map, searchMap)).isEqualTo(builder);
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
        list.add("description");
        list.add("name");
        _appService.checkProperties(list);
    }

    @Test
    public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
        Map<String, SearchFields> map = new HashMap<>();
        QTaskEntity task = QTaskEntity.taskEntity;
        List<SearchFields> fieldsList = new ArrayList<>();
        SearchFields fields = new SearchFields();
        SearchCriteria search = new SearchCriteria();
        search.setType(3);
        search.setValue("xyz");
        search.setOperator("equals");
        fields.setFieldName("description");
        fields.setOperator("equals");
        fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(task.description.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
        Mockito
            .doReturn(builder)
            .when(_appService)
            .searchKeyValuePair(any(QTaskEntity.class), any(HashMap.class), any(HashMap.class));

        Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
    }

    @Test
    public void search_StringIsNull_ReturnNull() throws Exception {
        Assertions.assertThat(_appService.search(null)).isEqualTo(null);
    }

    //Project
    @Test
    public void GetProject_IfTaskIdAndProjectIdIsNotNullAndTaskExists_ReturnProject() {
        TaskEntity task = mock(TaskEntity.class);
        Optional<TaskEntity> taskOptional = Optional.of((TaskEntity) task);
        ProjectEntity projectEntity = mock(ProjectEntity.class);

        Mockito.when(_taskRepository.findById(any(Long.class))).thenReturn(taskOptional);
        Mockito.when(task.getProject()).thenReturn(projectEntity);
        Assertions
            .assertThat(_appService.getProject(ID))
            .isEqualTo(_mapper.projectEntityToGetProjectOutput(projectEntity, task));
    }

    @Test
    public void GetProject_IfTaskIdAndProjectIdIsNotNullAndTaskDoesNotExist_ReturnNull() {
        Optional<TaskEntity> nullOptional = Optional.ofNullable(null);
        Mockito.when(_taskRepository.findById(anyLong())).thenReturn(nullOptional);
        Assertions.assertThat(_appService.getProject(ID)).isEqualTo(null);
    }

    @Test
    public void ParsetimesheetdetailsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("taskid", keyString);
        Assertions.assertThat(_appService.parseTimesheetdetailsJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }

    @Test
    public void ParseusertasksJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull() {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        String keyString = "15";
        joinColumnMap.put("taskid", keyString);
        Assertions.assertThat(_appService.parseUsertasksJoinColumn(keyString)).isEqualTo(joinColumnMap);
    }
}
