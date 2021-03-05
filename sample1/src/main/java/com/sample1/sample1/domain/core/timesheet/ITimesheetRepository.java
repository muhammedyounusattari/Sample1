package com.sample1.sample1.domain.core.timesheet;

import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("timesheetRepository")
public interface ITimesheetRepository
    extends JpaRepository<TimesheetEntity, Long>, QuerydslPredicateExecutor<TimesheetEntity> {}
