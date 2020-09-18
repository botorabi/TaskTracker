/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.user.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
public interface TaskRepository extends CrudRepository<Task, Long> {

    Optional<Task> findTaskByTitle(@NonNull final String title);

    @Query("select entry " +
            "from net.vrfun.tasktracker.task.Progress entry inner join " +
            "net.vrfun.tasktracker.task.Task task where task.id = :taskId")
    List<Progress> findProgressEntries(@NonNull final Long taskId);

    @Query("select task " +
            "from net.vrfun.tasktracker.task.Task task where :user member of task.users")
    List<Task> findUserTasks(@NonNull final User user);

    @Query("select task " +
            "from net.vrfun.tasktracker.task.Task task where :team member of task.teams")
    List<Task> findTeamTasks(@NonNull final Team team);

    @Query("select new net.vrfun.tasktracker.task.TaskShortInfo(task) " +
            "from net.vrfun.tasktracker.task.Task task where task.title like concat('%',:filter,'%')")
    List<TaskShortInfo> searchTask(@NonNull @Param("filter") final String filter);
}
