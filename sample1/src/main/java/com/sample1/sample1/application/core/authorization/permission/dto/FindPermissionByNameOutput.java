package com.sample1.sample1.application.core.authorization.permission.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPermissionByNameOutput {

    private String displayName;
    private Long id;
    private String name;
}
