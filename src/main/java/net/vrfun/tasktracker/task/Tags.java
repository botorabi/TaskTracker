/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Utilities for handling with Tags
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Service
public class Tags {

    private final TagRepository tagRepository;

    @Autowired
    public Tags(@NonNull final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @NonNull
    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();
        tagRepository.findAll().forEach(tags::add);
        return tags;
    }

    @Nullable
    public Tag get(long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        return tag.orElse(null);
    }

    @Nullable
    public List<Tag> getSimilarTags(@NonNull final String name) {
        return tagRepository.findSimilarTags(name);
    }

    @NonNull
    public Tag getOrCreate(@NonNull final String name) {
        Optional<Tag> tag = tagRepository.findTagByName(name);
        return tag.orElseGet(() -> tagRepository.save(new Tag(name)));
    }

    public void delete(@NonNull final String name) throws IllegalArgumentException {
        Optional<Tag> tag = tagRepository.findTagByName(name);
        if (tag.isPresent()) {
            tagRepository.delete(tag.get());
        }
        else {
            throw new IllegalArgumentException("Tag does not exist!");
        }
    }
}
