package com.sample1.sample1.application.core.usertask;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.usertask.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.task.ITaskRepository;
import com.sample1.sample1.domain.core.task.TaskEntity;
import com.sample1.sample1.domain.core.usertask.IUsertaskRepository;
import com.sample1.sample1.domain.core.usertask.QUsertaskEntity;
import com.sample1.sample1.domain.core.usertask.UsertaskEntity;
import com.sample1.sample1.domain.core.usertask.UsertaskId;
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

@Service("usertaskAppService")
@RequiredArgsConstructor
public class UsertaskAppService implements IUsertaskAppService {

    @Qualifier("usertaskRepository")
    @NonNull
    protected final IUsertaskRepository _usertaskRepository;

    @Qualifier("taskRepository")
    @NonNull
    protected final ITaskRepository _taskRepository;

    @Qualifier("usersRepository")
    @NonNull
    protected final IUsersRepository _usersRepository;

    @Qualifier("IUsertaskMapperImpl")
    @NonNull
    protected final IUsertaskMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateUsertaskOutput create(CreateUsertaskInput input) {
        UsertaskEntity usertask = mapper.createUsertaskInputToUsertaskEntity(input);
        TaskEntity foundTask = null;
        UsersEntity foundUsers = null;
        if (input.getTaskid() != null) {
            foundTask = _taskRepository.findById(input.getTaskid()).orElse(null);

            if (foundTask != null) {
                foundTask.addUsertasks(usertask);
                //usertask.setTask(foundTask);
            }
        }
        if (input.getUserid() != null) {
            foundUsers = _usersRepository.findById(input.getUserid()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addUsertasks(usertask);
                //usertask.setUsers(foundUsers);
            }
        }

        UsertaskEntity createdUsertask = _usertaskRepository.save(usertask);
        return mapper.usertaskEntityToCreateUsertaskOutput(createdUsertask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateUsertaskOutput update(UsertaskId usertaskId, UpdateUsertaskInput input) {
        UsertaskEntity existing = _usertaskRepository.findById(usertaskId).get();

        UsertaskEntity usertask = mapper.updateUsertaskInputToUsertaskEntity(input);
        TaskEntity foundTask = null;
        UsersEntity foundUsers = null;

        if (input.getTaskid() != null) {
            foundTask = _taskRepository.findById(input.getTaskid()).orElse(null);

            if (foundTask != null) {
                foundTask.addUsertasks(usertask);
                //	usertask.setTask(foundTask);
            }
        }

        if (input.getUserid() != null) {
            foundUsers = _usersRepository.findById(input.getUserid()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addUsertasks(usertask);
                //	usertask.setUsers(foundUsers);
            }
        }

        UsertaskEntity updatedUsertask = _usertaskRepository.save(usertask);
        return mapper.usertaskEntityToUpdateUsertaskOutput(updatedUsertask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(UsertaskId usertaskId) {
        UsertaskEntity existing = _usertaskRepository.findById(usertaskId).orElse(null);

        if (existing.getTask() != null) {
            existing.getTask().removeUsertasks(existing);
        }
        if (existing.getUsers() != null) {
            existing.getUsers().removeUsertasks(existing);
        }
        _usertaskRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsertaskByIdOutput findById(UsertaskId usertaskId) {
        UsertaskEntity foundUsertask = _usertaskRepository.findById(usertaskId).orElse(null);
        if (foundUsertask == null) return null;

        return mapper.usertaskEntityToFindUsertaskByIdOutput(foundUsertask);
    }

    //Task
    // ReST API Call - GET /usertask/1/task
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetTaskOutput getTask(UsertaskId usertaskId) {
        UsertaskEntity foundUsertask = _usertaskRepository.findById(usertaskId).orElse(null);
        if (foundUsertask == null) {
            logHelper.getLogger().error("There does not exist a usertask wth a id=%s", usertaskId);
            return null;
        }
        TaskEntity re = foundUsertask.getTask();
        return mapper.taskEntityToGetTaskOutput(re, foundUsertask);
    }

    //Users
    // ReST API Call - GET /usertask/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(UsertaskId usertaskId) {
        UsertaskEntity foundUsertask = _usertaskRepository.findById(usertaskId).orElse(null);
        if (foundUsertask == null) {
            logHelper.getLogger().error("There does not exist a usertask wth a id=%s", usertaskId);
            return null;
        }
        UsersEntity re = foundUsertask.getUsers();
        return mapper.usersEntityToGetUsersOutput(re, foundUsertask);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindUsertaskByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<UsertaskEntity> foundUsertask = _usertaskRepository.findAll(search(search), pageable);
        List<UsertaskEntity> usertaskList = foundUsertask.getContent();
        Iterator<UsertaskEntity> usertaskIterator = usertaskList.iterator();
        List<FindUsertaskByIdOutput> output = new ArrayList<>();

        while (usertaskIterator.hasNext()) {
            UsertaskEntity usertask = usertaskIterator.next();
            output.add(mapper.usertaskEntityToFindUsertaskByIdOutput(usertask));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QUsertaskEntity usertask = QUsertaskEntity.usertaskEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(usertask, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("task") ||
                    //		        list.get(i).replace("%20","").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    //		        list.get(i).replace("%20","").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("taskid") ||
                    list.get(i).replace("%20", "").trim().equals("userid")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QUsertaskEntity usertask,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("taskid")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(usertask.taskid.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.taskid.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.taskid.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            usertask.taskid.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(usertask.taskid.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(usertask.taskid.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("userid")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(usertask.userid.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.userid.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.userid.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            usertask.userid.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(usertask.userid.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(usertask.userid.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }

            if (details.getKey().replace("%20", "").trim().equals("task")) {
                if (
                    details.getValue().getOperator().equals("contains") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.task.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.task.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.task.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            usertask.task.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(usertask.task.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(usertask.task.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("users")) {
                if (
                    details.getValue().getOperator().equals("contains") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.users.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.users.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(usertask.users.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            usertask.users.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(usertask.users.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(usertask.users.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("taskid")) {
                builder.and(usertask.task.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("task")) {
                builder.and(usertask.task.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("userid")) {
                builder.and(usertask.users.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("users")) {
                builder.and(usertask.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public UsertaskId parseUsertaskKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        UsertaskId usertaskId = new UsertaskId();

        Map<String, String> keyMap = new HashMap<String, String>();
        if (keyEntries.length > 1) {
            for (String keyEntry : keyEntries) {
                String[] keyEntryArr = keyEntry.split("=");
                if (keyEntryArr.length > 1) {
                    keyMap.put(keyEntryArr[0], keyEntryArr[1]);
                } else {
                    return null;
                }
            }
        } else {
            String[] keyEntryArr = keysString.split("=");
            if (keyEntryArr.length > 1) {
                keyMap.put(keyEntryArr[0], keyEntryArr[1]);
            } else return null;
        }

        usertaskId.setTaskid(Long.valueOf(keyMap.get("taskid")));
        usertaskId.setUserid(Long.valueOf(keyMap.get("userid")));
        return usertaskId;
    }
}
