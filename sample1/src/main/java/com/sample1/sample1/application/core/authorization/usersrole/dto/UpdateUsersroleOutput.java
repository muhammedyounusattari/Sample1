package com.sample1.sample1.application.core.authorization.usersrole.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsersroleOutput {

    private Long roleId;
    private Long usersId;
    private String roleDescriptiveField;
    private Long usersDescriptiveField;
}
