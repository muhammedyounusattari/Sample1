package com.sample1.sample1.application.core.authorization.userspermission;

import com.sample1.sample1.application.core.authorization.userspermission.dto.*;
import com.sample1.sample1.domain.core.authorization.permission.PermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import java.time.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IUserspermissionMapper {
    UserspermissionEntity createUserspermissionInputToUserspermissionEntity(
        CreateUserspermissionInput userspermissionDto
    );

    @Mappings(
        {
            @Mapping(source = "users.id", target = "usersId"),
            @Mapping(source = "users.username", target = "usersDescriptiveField"),
            @Mapping(source = "permission.name", target = "permissionDescriptiveField"),
            @Mapping(source = "permission.id", target = "permissionId"),
        }
    )
    CreateUserspermissionOutput userspermissionEntityToCreateUserspermissionOutput(UserspermissionEntity entity);

    UserspermissionEntity updateUserspermissionInputToUserspermissionEntity(
        UpdateUserspermissionInput userspermissionDto
    );

    @Mappings(
        {
            @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),
            @Mapping(source = "entity.users.id", target = "usersDescriptiveField"),
        }
    )
    UpdateUserspermissionOutput userspermissionEntityToUpdateUserspermissionOutput(UserspermissionEntity entity);

    @Mappings(
        {
            @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),
            @Mapping(source = "entity.users.id", target = "usersDescriptiveField"),
        }
    )
    FindUserspermissionByIdOutput userspermissionEntityToFindUserspermissionByIdOutput(UserspermissionEntity entity);

    @Mappings(
        {
            @Mapping(source = "foundUserspermission.permissionId", target = "userspermissionPermissionId"),
            @Mapping(source = "foundUserspermission.usersId", target = "userspermissionUsersId"),
        }
    )
    GetPermissionOutput permissionEntityToGetPermissionOutput(
        PermissionEntity permission,
        UserspermissionEntity foundUserspermission
    );

    @Mappings(
        {
            @Mapping(source = "foundUserspermission.permissionId", target = "userspermissionPermissionId"),
            @Mapping(source = "foundUserspermission.usersId", target = "userspermissionUsersId"),
        }
    )
    GetUsersOutput usersEntityToGetUsersOutput(UsersEntity users, UserspermissionEntity foundUserspermission);
}
