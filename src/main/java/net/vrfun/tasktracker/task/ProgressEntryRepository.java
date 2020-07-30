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
import java.util.Optional;

@Transactional
public interface ProgressEntryRepository extends CrudRepository<ProgressEntry, Long> {

    Optional<ProgressEntry> findProgressEntryByOwnerName(@NonNull String ownerName);
}
