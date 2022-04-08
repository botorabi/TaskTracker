/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
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
public interface TagRepository extends CrudRepository<Tag, Long> {

    Optional<Tag> findTagByName(@NonNull String name);

    @Query("select tag " +
            "from net.vrfun.tasktracker.task.Tag tag where tag.name like concat('%',:tagName,'%')")
    List<Tag> findSimilarTags(@NonNull final String tagName);

    @Query("select entry " +
            "from net.vrfun.tasktracker.task.Progress entry inner join " +
            "net.vrfun.tasktracker.task.Tag tag where tag.name = :tagName")
    List<Progress> findAllTaggedProgressEntries(@NonNull final String tagName);
}
