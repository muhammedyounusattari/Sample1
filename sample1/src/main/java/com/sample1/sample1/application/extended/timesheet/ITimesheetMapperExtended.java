package com.sample1.sample1.application.extended.timesheet;

import com.sample1.sample1.application.core.timesheet.ITimesheetMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITimesheetMapperExtended extends ITimesheetMapper {}
