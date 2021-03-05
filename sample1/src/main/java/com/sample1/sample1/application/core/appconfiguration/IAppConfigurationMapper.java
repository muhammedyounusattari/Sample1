package com.sample1.sample1.application.core.appconfiguration;

import com.sample1.sample1.application.core.appconfiguration.dto.*;
import com.sample1.sample1.domain.core.appconfiguration.AppConfigurationEntity;
import java.time.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IAppConfigurationMapper {
    AppConfigurationEntity createAppConfigurationInputToAppConfigurationEntity(
        CreateAppConfigurationInput appconfigurationDto
    );
    CreateAppConfigurationOutput appConfigurationEntityToCreateAppConfigurationOutput(AppConfigurationEntity entity);

    AppConfigurationEntity updateAppConfigurationInputToAppConfigurationEntity(
        UpdateAppConfigurationInput appConfigurationDto
    );

    UpdateAppConfigurationOutput appConfigurationEntityToUpdateAppConfigurationOutput(AppConfigurationEntity entity);

    FindAppConfigurationByIdOutput appConfigurationEntityToFindAppConfigurationByIdOutput(
        AppConfigurationEntity entity
    );
}
