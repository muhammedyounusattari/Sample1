package com.sample1.sample1.domain.core.timesheetstatus;

import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("timesheetstatusRepository")
public interface ITimesheetstatusRepository
    extends JpaRepository<TimesheetstatusEntity, Long>, QuerydslPredicateExecutor<TimesheetstatusEntity> {}
