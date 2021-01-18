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
public class TaskTest {

    @Before
    public void setup() {
    }

    @Test
    public void defaultConstruction() {
        Task task = new Task();

        assertThat(task.getId()).isNull();
        assertThat(task.getDateCreation()).isNull();
        assertThat(task.getDateClosed()).isNull();
        assertThat(task.getTitle()).isNull();
        assertThat(task.getDescription()).isNull();
    }

    @Test
    public void constructionWithTitle() {
        final String title = "TestTask";
        Task task = new Task(title);

        assertThat(task.getId()).isNull();
        assertThat(task.getDateCreation()).isNotNull();
        assertThat(task.getDateClosed()).isNull();
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isNull();
    }

    @Test
    public void setterGetter() {
        Task task = new Task();

        final long id = 42;
        final Instant creationDate = Instant.now();
        final Instant closedDate = Instant.now();
        final String title = "TestTask";
        final String description = "My Description";

        task.setId(id);
        task.setDateCreation(creationDate);
        task.setDateClosed(closedDate);
        task.setTitle(title);
        task.setDescription(description);

        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getDateCreation()).isEqualTo(creationDate);
        assertThat(task.getDateClosed()).isEqualTo(closedDate);
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
    }
}
