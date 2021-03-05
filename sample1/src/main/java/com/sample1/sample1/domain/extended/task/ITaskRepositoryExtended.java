package com.sample1.sample1.domain.extended.task;

import com.sample1.sample1.domain.core.task.ITaskRepository;
import org.springframework.stereotype.Repository;

@Repository("taskRepositoryExtended")
public interface ITaskRepositoryExtended extends ITaskRepository {
    //Add your custom code here
}
