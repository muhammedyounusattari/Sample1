package com.sample1.sample1.application.extended.customer;

import com.sample1.sample1.application.core.customer.CustomerAppService;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.domain.extended.customer.ICustomerRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("customerAppServiceExtended")
public class CustomerAppServiceExtended extends CustomerAppService implements ICustomerAppServiceExtended {

    public CustomerAppServiceExtended(
        ICustomerRepositoryExtended customerRepositoryExtended,
        ICustomerMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(customerRepositoryExtended, mapper, logHelper);
    }
    //Add your custom code here

}
