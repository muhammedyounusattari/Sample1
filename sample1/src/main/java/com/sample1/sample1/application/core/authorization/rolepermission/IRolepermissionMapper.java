package com.sample1.sample1.application.core.authorization.rolepermission;

import com.sample1.sample1.application.core.authorization.rolepermission.dto.*;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.role.RoleEntity;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionEntity;
import java.time.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IRolepermissionMapper {
    RolepermissionEntity createRolepermissionInputToRolepermissionEntity(CreateRolepermissionInput rolepermissionDto);

    @Mappings(
        {
            @Mapping(source = "permission.displayName", target = "permissionDescriptiveField"),
            @Mapping(source = "permission.id", target = "permissionId"),
            @Mapping(source = "role.displayName", target = "roleDescriptiveField"),
            @Mapping(source = "role.id", target = "roleId"),
        }
    )
    CreateRolepermissionOutput rolepermissionEntityToCreateRolepermissionOutput(RolepermissionEntity entity);

    RolepermissionEntity updateRolepermissionInputToRolepermissionEntity(UpdateRolepermissionInput rolepermissionDto);

    @Mappings(
        {
            @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),
            @Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),
        }
    )
    UpdateRolepermissionOutput rolepermissionEntityToUpdateRolepermissionOutput(RolepermissionEntity entity);

    @Mappings(
        {
            @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),
            @Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),
        }
    )
    FindRolepermissionByIdOutput rolepermissionEntityToFindRolepermissionByIdOutput(RolepermissionEntity entity);

    @Mappings(
        {
            @Mapping(source = "foundRolepermission.permissionId", target = "rolepermissionPermissionId"),
            @Mapping(source = "foundRolepermission.roleId", target = "rolepermissionRoleId"),
        }
    )
    GetPermissionOutput permissionEntityToGetPermissionOutput(
        PermissionEntity permission,
        RolepermissionEntity foundRolepermission
    );

    @Mappings(
        {
            @Mapping(source = "foundRolepermission.permissionId", target = "rolepermissionPermissionId"),
            @Mapping(source = "foundRolepermission.roleId", target = "rolepermissionRoleId"),
        }
    )
    GetRoleOutput roleEntityToGetRoleOutput(RoleEntity role, RolepermissionEntity foundRolepermission);
}
