package com.sample1.sample1.application.core.authorization.users.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserspreferenceOutput {

    private String firstname;
    private String triggerName;
    private Boolean isactive;
    private String emailaddress;
    private String lastname;
    private String password;
    private LocalDate joinDate;
    private String triggerGroup;
    private Long id;
    private String username;
    private Long usersId;
}
