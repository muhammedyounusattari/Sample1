package com.sample1.sample1.application.extended.task;

import com.sample1.sample1.application.core.task.ITaskMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITaskMapperExtended extends ITaskMapper {}
