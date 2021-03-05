package com.sample1.sample1.domain.core.customer;

import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("customerRepository")
public interface ICustomerRepository
    extends JpaRepository<CustomerEntity, Long>, QuerydslPredicateExecutor<CustomerEntity> {}
