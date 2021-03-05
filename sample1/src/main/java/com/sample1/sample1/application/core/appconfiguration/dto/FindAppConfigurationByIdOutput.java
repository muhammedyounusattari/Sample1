package com.sample1.sample1.application.core.appconfiguration.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindAppConfigurationByIdOutput {

    private Long id;
    private String type;
    private String value;
    private Long versiono;
}
