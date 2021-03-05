package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.appconfiguration.IAppConfigurationAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.AppConfigurationController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appConfiguration/extended")
public class AppConfigurationControllerExtended extends AppConfigurationController {

    public AppConfigurationControllerExtended(
        IAppConfigurationAppServiceExtended appConfigurationAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(appConfigurationAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
