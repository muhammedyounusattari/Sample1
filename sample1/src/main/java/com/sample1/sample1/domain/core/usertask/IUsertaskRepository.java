package com.sample1.sample1.domain.core.usertask;

import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("usertaskRepository")
public interface IUsertaskRepository
    extends JpaRepository<UsertaskEntity, UsertaskId>, QuerydslPredicateExecutor<UsertaskEntity> {}
