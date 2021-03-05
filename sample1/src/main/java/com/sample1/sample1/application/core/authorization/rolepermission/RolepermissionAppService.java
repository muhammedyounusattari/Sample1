package com.sample1.sample1.application.core.authorization.rolepermission;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.authorization.rolepermission.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.permission.IPermissionRepository;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.role.IRoleRepository;
import com.sample1.sample1.domain.core.authorization.role.RoleEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.IRolepermissionRepository;
import com.sample1.sample1.domain.core.authorization.rolepermission.QRolepermissionEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionId;
import com.sample1.sample1.domain.core.authorization.usersrole.IUsersroleRepository;
import com.sample1.sample1.domain.core.authorization.usersrole.UsersroleEntity;
import com.sample1.sample1.security.JWTAppService;
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

@Service("rolepermissionAppService")
@RequiredArgsConstructor
public class RolepermissionAppService implements IRolepermissionAppService {

    @NonNull
    protected final JWTAppService _jwtAppService;

    @Qualifier("usersroleRepository")
    @NonNull
    protected final IUsersroleRepository _usersroleRepository;

    @Qualifier("rolepermissionRepository")
    @NonNull
    protected final IRolepermissionRepository _rolepermissionRepository;

    @Qualifier("permissionRepository")
    @NonNull
    protected final IPermissionRepository _permissionRepository;

    @Qualifier("roleRepository")
    @NonNull
    protected final IRoleRepository _roleRepository;

    @Qualifier("IRolepermissionMapperImpl")
    @NonNull
    protected final IRolepermissionMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateRolepermissionOutput create(CreateRolepermissionInput input) {
        RolepermissionEntity rolepermission = mapper.createRolepermissionInputToRolepermissionEntity(input);
        PermissionEntity foundPermission = null;
        RoleEntity foundRole = null;
        if (input.getPermissionId() != null) {
            foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
        } else {
            return null;
        }
        if (input.getRoleId() != null) {
            foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
        } else {
            return null;
        }

        if (foundPermission != null && foundRole != null) {
            if (!checkIfPermissionAlreadyAssigned(foundRole, foundPermission)) {
                rolepermission.setPermission(foundPermission);
                rolepermission.setRole(foundRole);
            }
        } else {
            return null;
        }

        RolepermissionEntity createdRolepermission = _rolepermissionRepository.save(rolepermission);
        return mapper.rolepermissionEntityToCreateRolepermissionOutput(createdRolepermission);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateRolepermissionOutput update(RolepermissionId rolepermissionId, UpdateRolepermissionInput input) {
        RolepermissionEntity existing = _rolepermissionRepository.findById(rolepermissionId).get();

        RolepermissionEntity rolepermission = mapper.updateRolepermissionInputToRolepermissionEntity(input);
        PermissionEntity foundPermission = null;
        RoleEntity foundRole = null;

        if (input.getPermissionId() != null) {
            foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
        } else {
            return null;
        }

        if (input.getRoleId() != null) {
            foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
        } else {
            return null;
        }

        if (foundPermission != null && foundRole != null) {
            if (checkIfPermissionAlreadyAssigned(foundRole, foundPermission)) {
                foundPermission.addRolepermissions(rolepermission);
                foundRole.addRolepermissions(rolepermission);
                //rolepermission.setPermission(foundPermission);
                //rolepermission.setRole(foundRole);
            }
        } else {
            return null;
        }

        RolepermissionEntity updatedRolepermission = _rolepermissionRepository.save(rolepermission);
        return mapper.rolepermissionEntityToUpdateRolepermissionOutput(updatedRolepermission);
    }

    protected boolean checkIfPermissionAlreadyAssigned(RoleEntity foundRole, PermissionEntity foundPermission) {
        List<RolepermissionEntity> rolePermissionList = _rolepermissionRepository.findByRoleId(foundRole.getId());

        Iterator pIterator = rolePermissionList.iterator();
        while (pIterator.hasNext()) {
            RolepermissionEntity pe = (RolepermissionEntity) pIterator.next();
            if (pe.getPermission() == foundPermission) {
                return true;
            }
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(RolepermissionId rolepermissionId) {
        RolepermissionEntity existing = _rolepermissionRepository.findById(rolepermissionId).orElse(null);

        if (existing.getPermission() != null) {
            existing.getPermission().removeRolepermissions(existing);
        }
        if (existing.getRole() != null) {
            existing.getRole().removeRolepermissions(existing);
        }
        _rolepermissionRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindRolepermissionByIdOutput findById(RolepermissionId rolepermissionId) {
        RolepermissionEntity foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
        if (foundRolepermission == null) return null;

        return mapper.rolepermissionEntityToFindRolepermissionByIdOutput(foundRolepermission);
    }

    public void deleteUserTokens(Long roleId) {
        RoleEntity role = _roleRepository.findById(roleId).orElse(null);
        List<UsersroleEntity> userRole = _usersroleRepository.findByRoleId(roleId);

        for (UsersroleEntity ur : userRole) {
            if (ur.getUsers() != null) _jwtAppService.deleteAllUserTokens(ur.getUsers().getUsername());
        }
    }

    //Permission
    // ReST API Call - GET /rolepermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetPermissionOutput getPermission(RolepermissionId rolepermissionId) {
        RolepermissionEntity foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
        if (foundRolepermission == null) {
            logHelper.getLogger().error("There does not exist a rolepermission wth a id=%s", rolepermissionId);
            return null;
        }
        PermissionEntity re = foundRolepermission.getPermission();
        return mapper.permissionEntityToGetPermissionOutput(re, foundRolepermission);
    }

    //Role
    // ReST API Call - GET /rolepermission/1/role
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetRoleOutput getRole(RolepermissionId rolepermissionId) {
        RolepermissionEntity foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
        if (foundRolepermission == null) {
            logHelper.getLogger().error("There does not exist a rolepermission wth a id=%s", rolepermissionId);
            return null;
        }
        RoleEntity re = foundRolepermission.getRole();
        return mapper.roleEntityToGetRoleOutput(re, foundRolepermission);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindRolepermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<RolepermissionEntity> foundRolepermission = _rolepermissionRepository.findAll(search(search), pageable);
        List<RolepermissionEntity> rolepermissionList = foundRolepermission.getContent();
        Iterator<RolepermissionEntity> rolepermissionIterator = rolepermissionList.iterator();
        List<FindRolepermissionByIdOutput> output = new ArrayList<>();

        while (rolepermissionIterator.hasNext()) {
            RolepermissionEntity rolepermission = rolepermissionIterator.next();
            output.add(mapper.rolepermissionEntityToFindRolepermissionByIdOutput(rolepermission));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QRolepermissionEntity rolepermission = QRolepermissionEntity.rolepermissionEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(rolepermission, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("permission") ||
                    //		        list.get(i).replace("%20","").trim().equals("displayName") ||
                    list.get(i).replace("%20", "").trim().equals("role") ||
                    //		        list.get(i).replace("%20","").trim().equals("displayName") ||
                    list.get(i).replace("%20", "").trim().equals("permissionId") ||
                    list.get(i).replace("%20", "").trim().equals("roleId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QRolepermissionEntity rolepermission,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("permissionId")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(rolepermission.permissionId.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(rolepermission.permissionId.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(rolepermission.permissionId.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            rolepermission.permissionId.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(
                            rolepermission.permissionId.goe(Long.valueOf(details.getValue().getStartingValue()))
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(rolepermission.permissionId.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("roleId")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(rolepermission.roleId.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(rolepermission.roleId.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(rolepermission.roleId.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            rolepermission.roleId.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(rolepermission.roleId.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(rolepermission.roleId.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }

            if (details.getKey().replace("%20", "").trim().equals("permission")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(
                        rolepermission.permission.displayName.likeIgnoreCase(
                            "%" + details.getValue().getSearchValue() + "%"
                        )
                    );
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(rolepermission.permission.displayName.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(rolepermission.permission.displayName.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("role")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(
                        rolepermission.role.displayName.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                    );
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(rolepermission.role.displayName.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(rolepermission.role.displayName.ne(details.getValue().getSearchValue()));
                }
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("permissionId")) {
                builder.and(rolepermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("permission")) {
                builder.and(rolepermission.permission.displayName.eq(joinCol.getValue()));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("roleId")) {
                builder.and(rolepermission.role.id.eq(Long.parseLong(joinCol.getValue())));
            }

            if (joinCol != null && joinCol.getKey().equals("role")) {
                builder.and(rolepermission.role.displayName.eq(joinCol.getValue()));
            }
        }
        return builder;
    }

    public RolepermissionId parseRolepermissionKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        RolepermissionId rolepermissionId = new RolepermissionId();

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

        rolepermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
        rolepermissionId.setRoleId(Long.valueOf(keyMap.get("roleId")));
        return rolepermissionId;
    }
}
