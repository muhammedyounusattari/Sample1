package com.sample1.sample1.application.extended.customer;

import com.sample1.sample1.application.core.customer.ICustomerMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICustomerMapperExtended extends ICustomerMapper {}
