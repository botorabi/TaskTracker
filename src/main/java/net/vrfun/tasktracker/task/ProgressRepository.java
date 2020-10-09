/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Transactional
public interface ProgressRepository extends CrudRepository<Progress, Long> {

    List<Progress> findAllByOrderByReportWeekDesc(@NonNull final Pageable pageable);

    long countProgressByOwnerId(@NonNull final Long ownerId);

    List<Progress> findProgressByOwnerIdOrderByReportWeekDesc(@NonNull final Long ownerId, @NonNull final Pageable pageable);

    List<Progress> findByTaskId(@NonNull final Long id);

    List<Progress> findByTaskIdAndReportWeekBetween(@NonNull final Long taskId,
                                                    @NonNull final LocalDate fromDate,
                                                    @NonNull final LocalDate toDate);
}
