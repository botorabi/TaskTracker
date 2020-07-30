/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
public interface TaskRepository extends CrudRepository<Task, Long> {

    Optional<Task> findTaskByTitle(@NonNull String title);

    @Query("select entry " +
            "from net.vrfun.tasktracker.task.ProgressEntry entry inner join " +
            "net.vrfun.tasktracker.task.Task task where task.id = :taskId")
    List<ProgressEntry> findAllAssociatedProgressEntries(@NonNull Long taskId);
}
