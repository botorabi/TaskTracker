/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
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
public class ProgressTest {

    @Before
    public void setup() {
    }

    @Test
    public void defaultConstruction() {
        Progress progress = new Progress();

        assertThat(progress.getId()).isNull();
        assertThat(progress.getOwnerName()).isNull();
        assertThat(progress.getDateCreation()).isNull();
        assertThat(progress.getTags()).isNull();
        assertThat(progress.getTask()).isNull();
        assertThat(progress.getText()).isNull();
    }

    @Test
    public void constructionWithName() {
        final String ownerName = "TestTag";
        final Long ownerId = 42L;

        Progress progress = new Progress(ownerName, ownerId);

        assertThat(progress.getId()).isNull();
        assertThat(progress.getOwnerName()).isEqualTo(ownerName);
        assertThat(progress.getDateCreation()).isNotNull();
        assertThat(progress.getText()).isNull();
        assertThat(progress.getTags()).isNull();
        assertThat(progress.getTask()).isNull();
        assertThat(progress.getText()).isNull();
    }

    @Test
    public void getterSetter() {
        Progress progress = new Progress();

        final long id = 42L;
        final String ownerName = "TestTag";
        final Long ownerId = 142L;
        final String text = "My Text";
        final Task task = new Task();
        task.setId(442L);
        final Instant date = Instant.now();

        progress.setId(id);
        progress.setOwnerName(ownerName);
        progress.setOwnerId(ownerId);
        progress.setDateCreation(date);
        progress.setTags(new ArrayList<>());
        progress.setTask(task);
        progress.setText(text);

        assertThat(progress.getId()).isEqualTo(id);
        assertThat(progress.getOwnerName()).isEqualTo(ownerName);
        assertThat(progress.getOwnerId()).isEqualTo(ownerId);
        assertThat(progress.getDateCreation()).isEqualTo(date);
        assertThat(progress.getText()).isEqualTo(text);
        assertThat(progress.getTags()).isNotNull();
        assertThat(progress.getTask()).isEqualTo(task);
    }
}
