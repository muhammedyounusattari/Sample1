package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.application.extended.customer.ICustomerAppServiceExtended;
import com.sample1.sample1.application.extended.project.IProjectAppServiceExtended;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.restcontrollers.core.CustomerController;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/extended")
public class CustomerControllerExtended extends CustomerController {

    public CustomerControllerExtended(
        ICustomerAppServiceExtended customerAppServiceExtended,
        IProjectAppServiceExtended projectAppServiceExtended,
        LoggingHelper helper,
        Environment env
    ) {
        super(customerAppServiceExtended, projectAppServiceExtended, helper, env);
    }
    //Add your custom code here

}
