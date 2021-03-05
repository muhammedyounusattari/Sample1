package com.sample1.sample1.application.core.authorization.role;

import com.sample1.sample1.application.core.authorization.role.dto.*;
import com.sample1.sample1.domain.core.authorization.role.RoleEntity;
import java.time.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
    RoleEntity createRoleInputToRoleEntity(CreateRoleInput roleDto);
    CreateRoleOutput roleEntityToCreateRoleOutput(RoleEntity entity);

    RoleEntity updateRoleInputToRoleEntity(UpdateRoleInput roleDto);

    UpdateRoleOutput roleEntityToUpdateRoleOutput(RoleEntity entity);

    FindRoleByIdOutput roleEntityToFindRoleByIdOutput(RoleEntity entity);

    FindRoleByNameOutput roleEntityToFindRoleByNameOutput(RoleEntity entity);
}
