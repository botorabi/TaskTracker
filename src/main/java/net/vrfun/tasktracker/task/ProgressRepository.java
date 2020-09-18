/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ProgressRepository extends CrudRepository<Progress, Long> {

    List<Progress> findProgressByOwnerId(@NonNull final Long ownerId);

    List<Progress> findProgressByOwnerName(@NonNull final String ownerName);

    List<Progress> findByTaskId(@NonNull final Long id);
}
