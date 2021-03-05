package com.sample1.sample1.application.core.customer;

import com.sample1.sample1.application.core.customer.dto.*;
import com.sample1.sample1.commons.search.SearchCriteria;
import java.util.*;
import org.springframework.data.domain.Pageable;

public interface ICustomerAppService {
    //CRUD Operations

    CreateCustomerOutput create(CreateCustomerInput customer);

    void delete(Long id);

    UpdateCustomerOutput update(Long id, UpdateCustomerInput input);

    FindCustomerByIdOutput findById(Long id);

    List<FindCustomerByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    //Join Column Parsers

    Map<String, String> parseProjectsJoinColumn(String keysString);
}
