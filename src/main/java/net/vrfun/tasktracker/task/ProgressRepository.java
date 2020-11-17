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

    long countProgressByOwnerIdIn(@NonNull final List<Long> ownerIds);

    List<Progress> findProgressByOwnerIdInOrderByReportWeekDesc(@NonNull final List<Long> ownerIds, @NonNull final Pageable pageable);

    long countProgressByTaskId(@NonNull final Long taskId);

    List<Progress> findByTaskId(@NonNull final Long id);

    List<Progress> findByTaskIdAndReportWeekBetween(@NonNull final Long taskId,
                                                    @NonNull final LocalDate fromDate,
                                                    @NonNull final LocalDate toDate);
}
