/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;


public class TagsTest {

    @Mock
    private TagRepository tagRepository;

    private Tags tags;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        tags = new Tags(tagRepository);
    }

    @Test
    public void getExistingTag() {
        doReturn(Optional.of(new Tag("Tag"))).when(tagRepository).findById(anyLong());

        assertThat(tags.get(42L)).isNotNull();
    }

    @Test
    public void getNonExistingTag() {
        doReturn(Optional.empty()).when(tagRepository).findById(anyLong());

        assertThat(tags.get(42L)).isNull();
    }

    @Test
    public void getAll() {
        List<Tag> allTags = new ArrayList<>();
        allTags.add(new Tag("Tag1"));
        allTags.add(new Tag("Tag2"));

        doReturn(allTags).when(tagRepository).findAll();

        assertThat(tags.getAll()).hasSize(allTags.size());
    }

    @Test
    public void createNewTags() {
        doReturn(Optional.of(new Tag())).when(tagRepository).findTagByName(anyString());

        assertThat(tags.getOrCreate("TestTag1")).isNotNull();
        assertThat(tags.getOrCreate("TestTag2")).isNotNull();
        assertThat(tags.getOrCreate("TestTag3")).isNotNull();
    }

    @Test
    public void getExistingTags() {
        doReturn(Optional.empty()).when(tagRepository).findTagByName(anyString());
        doReturn(new Tag()).when(tagRepository).save(any());

        assertThat(tags.getOrCreate("TestTag1")).isNotNull();
        assertThat(tags.getOrCreate("TestTag2")).isNotNull();
        assertThat(tags.getOrCreate("TestTag3")).isNotNull();
    }

    @Test
    public void deleteExistingTag() {
        doReturn(Optional.of(new Tag())).when(tagRepository).findTagByName(anyString());

        try {
            tags.delete("mytag");
        }
        catch(IllegalArgumentException ignored) {
            Assertions.fail("Failed to delete an existing tag.");
        }
    }

    @Test
    public void deleteNonExistingTag() {
        doReturn(Optional.empty()).when(tagRepository).findTagByName(anyString());
        try {
            tags.delete("mytag");
            Assertions.fail("Did not detect a non-existing tag during deletion.");
        }
        catch(IllegalArgumentException ignored) {
        }
    }
}
