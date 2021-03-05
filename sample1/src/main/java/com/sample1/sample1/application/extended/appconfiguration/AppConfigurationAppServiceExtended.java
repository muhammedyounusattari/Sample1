package com.sample1.sample1.application.extended.appconfiguration;

import com.sample1.sample1.application.core.appconfiguration.AppConfigurationAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.appconfiguration.IAppConfigurationRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("appConfigurationAppServiceExtended")
public class AppConfigurationAppServiceExtended
    extends AppConfigurationAppService
    implements IAppConfigurationAppServiceExtended {

    public AppConfigurationAppServiceExtended(
        IAppConfigurationRepositoryExtended appConfigurationRepositoryExtended,
        IAppConfigurationMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(appConfigurationRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
