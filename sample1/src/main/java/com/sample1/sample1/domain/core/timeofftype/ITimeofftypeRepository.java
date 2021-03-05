package com.sample1.sample1.domain.core.timeofftype;

import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("timeofftypeRepository")
public interface ITimeofftypeRepository
    extends JpaRepository<TimeofftypeEntity, Long>, QuerydslPredicateExecutor<TimeofftypeEntity> {}
