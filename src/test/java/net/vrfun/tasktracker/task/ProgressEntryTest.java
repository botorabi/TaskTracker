/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ProgressEntryTest {

    @Before
    public void setup() {
    }

    @Test
    public void defaultConstruction() {
        ProgressEntry progressEntry = new ProgressEntry();

        assertThat(progressEntry.getId()).isNull();
        assertThat(progressEntry.getOwnerName()).isNull();
        assertThat(progressEntry.getDate()).isNull();
        assertThat(progressEntry.getTags()).isNull();
        assertThat(progressEntry.getTask()).isNull();
        assertThat(progressEntry.getText()).isNull();
    }

    @Test
    public void constructionWithName() {
        final String ownerName = "TestTag";

        ProgressEntry progressEntry = new ProgressEntry(ownerName);

        assertThat(progressEntry.getId()).isNull();
        assertThat(progressEntry.getOwnerName()).isEqualTo(ownerName);
        assertThat(progressEntry.getDate()).isNotNull();
        assertThat(progressEntry.getText()).isNull();
        assertThat(progressEntry.getTags()).isNull();
        assertThat(progressEntry.getTask()).isNull();
        assertThat(progressEntry.getText()).isNull();
    }

    @Test
    public void getterSetter() {
        ProgressEntry progressEntry = new ProgressEntry();

        final long id = 42L;
        final String ownerName = "TestTag";
        final String text = "My Text";
        final Task task = new Task();
        task.setId(442L);
        final Instant date = Instant.now();

        progressEntry.setId(id);
        progressEntry.setOwnerName(ownerName);
        progressEntry.setDate(date);
        progressEntry.setTags(new ArrayList<>());
        progressEntry.setTask(task);
        progressEntry.setText(text);

        assertThat(progressEntry.getId()).isEqualTo(id);
        assertThat(progressEntry.getOwnerName()).isEqualTo(ownerName);
        assertThat(progressEntry.getDate()).isEqualTo(date);
        assertThat(progressEntry.getText()).isEqualTo(text);
        assertThat(progressEntry.getTags()).isNotNull();
        assertThat(progressEntry.getTask()).isEqualTo(task);
    }
}
