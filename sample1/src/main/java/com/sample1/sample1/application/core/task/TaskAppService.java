package com.sample1.sample1.application.core.task;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.task.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.project.IProjectRepository;
import com.sample1.sample1.domain.core.project.ProjectEntity;
import com.sample1.sample1.domain.core.task.ITaskRepository;
import com.sample1.sample1.domain.core.task.QTaskEntity;
import com.sample1.sample1.domain.core.task.TaskEntity;
import java.time.*;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("taskAppService")
@RequiredArgsConstructor
public class TaskAppService implements ITaskAppService {

    @Qualifier("taskRepository")
    @NonNull
    protected final ITaskRepository _taskRepository;

    @Qualifier("projectRepository")
    @NonNull
    protected final IProjectRepository _projectRepository;

    @Qualifier("ITaskMapperImpl")
    @NonNull
    protected final ITaskMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateTaskOutput create(CreateTaskInput input) {
        TaskEntity task = mapper.createTaskInputToTaskEntity(input);
        ProjectEntity foundProject = null;
        if (input.getProjectid() != null) {
            foundProject = _projectRepository.findById(input.getProjectid()).orElse(null);

            if (foundProject != null) {
                foundProject.addTasks(task);
                //task.setProject(foundProject);
            }
        }

        TaskEntity createdTask = _taskRepository.save(task);
        return mapper.taskEntityToCreateTaskOutput(createdTask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateTaskOutput update(Long taskId, UpdateTaskInput input) {
        TaskEntity existing = _taskRepository.findById(taskId).get();

        TaskEntity task = mapper.updateTaskInputToTaskEntity(input);
        task.setTimesheetdetailsSet(existing.getTimesheetdetailsSet());
        task.setUsertasksSet(existing.getUsertasksSet());
        ProjectEntity foundProject = null;

        if (input.getProjectid() != null) {
            foundProject = _projectRepository.findById(input.getProjectid()).orElse(null);

            if (foundProject != null) {
                foundProject.addTasks(task);
                //	task.setProject(foundProject);
            }
        }

        TaskEntity updatedTask = _taskRepository.save(task);
        return mapper.taskEntityToUpdateTaskOutput(updatedTask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long taskId) {
        TaskEntity existing = _taskRepository.findById(taskId).orElse(null);

        if (existing.getProject() != null) {
            existing.getProject().removeTasks(existing);
        }
        _taskRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindTaskByIdOutput findById(Long taskId) {
        TaskEntity foundTask = _taskRepository.findById(taskId).orElse(null);
        if (foundTask == null) return null;

        return mapper.taskEntityToFindTaskByIdOutput(foundTask);
    }

    //Project
    // ReST API Call - GET /task/1/project
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetProjectOutput getProject(Long taskId) {
        TaskEntity foundTask = _taskRepository.findById(taskId).orElse(null);
        if (foundTask == null) {
            logHelper.getLogger().error("There does not exist a task wth a id=%s", taskId);
            return null;
        }
        ProjectEntity re = foundTask.getProject();
        return mapper.projectEntityToGetProjectOutput(re, foundTask);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindTaskByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<TaskEntity> foundTask = _taskRepository.findAll(search(search), pageable);
        List<TaskEntity> taskList = foundTask.getContent();
        Iterator<TaskEntity> taskIterator = taskList.iterator();
        List<FindTaskByIdOutput> output = new ArrayList<>();

        while (taskIterator.hasNext()) {
            TaskEntity task = taskIterator.next();
            output.add(mapper.taskEntityToFindTaskByIdOutput(task));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QTaskEntity task = QTaskEntity.taskEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(task, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("project") ||
                    list.get(i).replace("%20", "").trim().equals("projectid") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("isactive") ||
                    list.get(i).replace("%20", "").trim().equals("name")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QTaskEntity task,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("description")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(task.description.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(task.description.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(task.description.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(task.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(task.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(task.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            task.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(task.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(task.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("isactive")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(task.isactive.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(task.isactive.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("name")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(task.name.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(task.name.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(task.name.ne(details.getValue().getSearchValue()));
                }
            }

            if (details.getKey().replace("%20", "").trim().equals("project")) {
                if (
                    details.getValue().getOperator().equals("contains") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(task.project.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(task.project.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(task.project.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            task.project.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(task.project.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(task.project.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("projectid")) {
                builder.and(task.project.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("project")) {
                builder.and(task.project.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public Map<String, String> parseTimesheetdetailsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("taskid", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUsertasksJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("taskid", keysString);

        return joinColumnMap;
    }
}
