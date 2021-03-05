package com.sample1.sample1.domain.extended.customer;

import com.sample1.sample1.domain.core.customer.ICustomerRepository;
import org.springframework.stereotype.Repository;

@Repository("customerRepositoryExtended")
public interface ICustomerRepositoryExtended extends ICustomerRepository {
    //Add your custom code here
}
