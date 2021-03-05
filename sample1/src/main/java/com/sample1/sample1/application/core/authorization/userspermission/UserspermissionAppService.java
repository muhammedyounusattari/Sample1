package com.sample1.sample1.application.core.authorization.userspermission;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.authorization.userspermission.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.permission.IPermissionRepository;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.IUserspermissionRepository;
import com.sample1.sample1.domain.core.authorization.userspermission.QUserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionId;
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

@Service("userspermissionAppService")
@RequiredArgsConstructor
public class UserspermissionAppService implements IUserspermissionAppService {

    @Qualifier("userspermissionRepository")
    @NonNull
    protected final IUserspermissionRepository _userspermissionRepository;

    @Qualifier("permissionRepository")
    @NonNull
    protected final IPermissionRepository _permissionRepository;

    @Qualifier("usersRepository")
    @NonNull
    protected final IUsersRepository _usersRepository;

    @Qualifier("IUserspermissionMapperImpl")
    @NonNull
    protected final IUserspermissionMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateUserspermissionOutput create(CreateUserspermissionInput input) {
        UserspermissionEntity userspermission = mapper.createUserspermissionInputToUserspermissionEntity(input);
        PermissionEntity foundPermission = null;
        UsersEntity foundUsers = null;
        if (input.getPermissionId() != null) {
            foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
        } else {
            return null;
        }
        if (input.getUsersId() != null) {
            foundUsers = _usersRepository.findById(input.getUsersId()).orElse(null);
        } else {
            return null;
        }
        if (foundUsers != null || foundPermission != null) {
            if (!checkIfPermissionAlreadyAssigned(foundUsers, foundPermission)) {
                foundPermission.addUserspermissions(userspermission);
                foundUsers.addUserspermissions(userspermission);
                //	userspermission.setPermission(foundPermission);
                //	userspermission.setUsers(foundUsers);
            }
        } else {
            return null;
        }

        UserspermissionEntity createdUserspermission = _userspermissionRepository.save(userspermission);
        CreateUserspermissionOutput output = mapper.userspermissionEntityToCreateUserspermissionOutput(
            createdUserspermission
        );

        output.setRevoked(input.getRevoked());
        return output;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateUserspermissionOutput update(UserspermissionId userspermissionId, UpdateUserspermissionInput input) {
        UserspermissionEntity existing = _userspermissionRepository.findById(userspermissionId).get();

        UserspermissionEntity userspermission = mapper.updateUserspermissionInputToUserspermissionEntity(input);
        PermissionEntity foundPermission = null;
        UsersEntity foundUsers = null;

        if (input.getPermissionId() != null) {
            foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
        } else {
            return null;
        }

        if (input.getUsersId() != null) {
            foundUsers = _usersRepository.findById(input.getUsersId()).orElse(null);
        } else {
            return null;
        }
        if (foundUsers != null || foundPermission != null) {
            if (checkIfPermissionAlreadyAssigned(foundUsers, foundPermission)) {
                //userspermission.setPermission(foundPermission);
                //userspermission.setUsers(foundUsers);
                userspermission.setRevoked(input.getRevoked());
                foundPermission.addUserspermissions(userspermission);
                foundUsers.addUserspermissions(userspermission);
            }
        } else {
            return null;
        }
        UserspermissionEntity updatedUserspermission = _userspermissionRepository.save(userspermission);
        return mapper.userspermissionEntityToUpdateUserspermissionOutput(updatedUserspermission);
    }

    public boolean checkIfPermissionAlreadyAssigned(UsersEntity foundUsers, PermissionEntity foundPermission) {
        List<UserspermissionEntity> usersPermission = _userspermissionRepository.findByUsersId(foundUsers.getId());

        Iterator pIterator = usersPermission.iterator();
        while (pIterator.hasNext()) {
            UserspermissionEntity pe = (UserspermissionEntity) pIterator.next();
            if (pe.getPermission() == foundPermission) {
                return true;
            }
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(UserspermissionId userspermissionId) {
        UserspermissionEntity existing = _userspermissionRepository.findById(userspermissionId).orElse(null);

        if (existing.getPermission() != null) {
            existing.getPermission().removeUserspermissions(existing);
        }
        if (existing.getUsers() != null) {
            existing.getUsers().removeUserspermissions(existing);
        }
        _userspermissionRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUserspermissionByIdOutput findById(UserspermissionId userspermissionId) {
        UserspermissionEntity foundUserspermission = _userspermissionRepository
            .findById(userspermissionId)
            .orElse(null);
        if (foundUserspermission == null) return null;

        return mapper.userspermissionEntityToFindUserspermissionByIdOutput(foundUserspermission);
    }

    //Permission
    // ReST API Call - GET /userspermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetPermissionOutput getPermission(UserspermissionId userspermissionId) {
        UserspermissionEntity foundUserspermission = _userspermissionRepository
            .findById(userspermissionId)
            .orElse(null);
        if (foundUserspermission == null) {
            logHelper.getLogger().error("There does not exist a userspermission wth a id=%s", userspermissionId);
            return null;
        }
        PermissionEntity re = foundUserspermission.getPermission();
        return mapper.permissionEntityToGetPermissionOutput(re, foundUserspermission);
    }

    //Users
    // ReST API Call - GET /userspermission/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(UserspermissionId userspermissionId) {
        UserspermissionEntity foundUserspermission = _userspermissionRepository
            .findById(userspermissionId)
            .orElse(null);
        if (foundUserspermission == null) {
            logHelper.getLogger().error("There does not exist a userspermission wth a id=%s", userspermissionId);
            return null;
        }
        UsersEntity re = foundUserspermission.getUsers();
        return mapper.usersEntityToGetUsersOutput(re, foundUserspermission);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindUserspermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<UserspermissionEntity> foundUserspermission = _userspermissionRepository.findAll(search(search), pageable);
        List<UserspermissionEntity> userspermissionList = foundUserspermission.getContent();
        Iterator<UserspermissionEntity> userspermissionIterator = userspermissionList.iterator();
        List<FindUserspermissionByIdOutput> output = new ArrayList<>();

        while (userspermissionIterator.hasNext()) {
            UserspermissionEntity userspermission = userspermissionIterator.next();
            output.add(mapper.userspermissionEntityToFindUserspermissionByIdOutput(userspermission));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QUserspermissionEntity userspermission = QUserspermissionEntity.userspermissionEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(userspermission, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("permission") ||
                    //		        list.get(i).replace("%20","").trim().equals("displayName") ||
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    //		        list.get(i).replace("%20","").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("permissionId") ||
                    list.get(i).replace("%20", "").trim().equals("revoked") ||
                    list.get(i).replace("%20", "").trim().equals("usersId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QUserspermissionEntity userspermission,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("permissionId")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(userspermission.permissionId.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.permissionId.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.permissionId.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            userspermission.permissionId.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(
                            userspermission.permissionId.goe(Long.valueOf(details.getValue().getStartingValue()))
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(
                            userspermission.permissionId.loe(Long.valueOf(details.getValue().getEndingValue()))
                        );
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("revoked")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(userspermission.revoked.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(userspermission.revoked.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("usersId")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(userspermission.usersId.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.usersId.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.usersId.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            userspermission.usersId.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(userspermission.usersId.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(userspermission.usersId.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }

            if (details.getKey().replace("%20", "").trim().equals("permission")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(
                        userspermission.permission.displayName.likeIgnoreCase(
                            "%" + details.getValue().getSearchValue() + "%"
                        )
                    );
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(userspermission.permission.displayName.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(userspermission.permission.displayName.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("users")) {
                if (
                    details.getValue().getOperator().equals("contains") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.users.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.users.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(userspermission.users.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            userspermission.users.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(userspermission.users.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(userspermission.users.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("permissionId")) {
                builder.and(userspermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("permission")) {
                builder.and(userspermission.permission.displayName.eq(joinCol.getValue()));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("usersId")) {
                builder.and(userspermission.users.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("users")) {
                builder.and(userspermission.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public UserspermissionId parseUserspermissionKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        UserspermissionId userspermissionId = new UserspermissionId();

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

        userspermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
        userspermissionId.setUsersId(Long.valueOf(keyMap.get("usersId")));
        return userspermissionId;
    }
}
