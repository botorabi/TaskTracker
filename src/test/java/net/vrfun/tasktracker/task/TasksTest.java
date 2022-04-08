/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;


import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;


public class TasksTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private UserAuthenticator userAuthenticator;

    private Tasks tasks;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        tasks = new Tasks(taskRepository, userRepository, teamRepository, progressRepository, userAuthenticator);
    }

    @Test
    public void getTasks() {
        Task task1 = new Task("Task1");
        task1.setId(42L);
        Task task2 = new Task("Task2");
        task2.setId(43L);
        List<Task> allTasks = Arrays.asList(task1, task2);

        doReturn(allTasks).when(taskRepository).findUserTasks(any());

        assertThat(tasks.getTasks()).hasSize(allTasks.size());
    }

    @Test
    public void getExistingTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        try {
            tasks.getTaskById(42L);
            fail("Failed to find an existing task.");
        }
        catch(Throwable ignored) {
        }
    }

    @Test
    public void getNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        try {
            tasks.getTaskById(42L);
            fail("Failed to detect non-existing task.");
        }
        catch(Throwable ignored) {
        }
    }

    @Test
    public void createExistingTitle() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setTitle("MyTitle");
        try {
            tasks.create(taskEdit);
            fail("Failed to detect existing title during task creation.");
        }
        catch (Throwable ignored) {
        }
    }

    @Test
    public void createInvalidTitle() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        try {
            tasks.create(taskEdit);
            fail("Failed to detect missing title during task creation.");
        }
        catch (Throwable ignored) {
        }
    }

    @Test
    public void createSuccess() {
        doReturn(Optional.empty()).when(taskRepository).findTaskByTitle(anyString());
        doReturn(new Task("MyTitle")).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setTitle("MyTitle");

        try {
            Task newTask = tasks.create(taskEdit);

            assertThat(newTask.getTitle()).isEqualTo("MyTitle");
            assertThat(newTask.getDateClosed()).isNull();
            assertThat(newTask.getDateCreation()).isNotNull();
        }
        catch (Throwable ignored) {
            fail("Failed to create a new task, reason: " + ignored.getMessage());
        }
    }

    @Test
    public void createOrCreateNewTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        assertThat(tasks.getOrCreate("TestTask1")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask2")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask3")).isNotNull();
    }

    @Test
    public void getOrCreateExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findTaskByTitle(anyString());
        doReturn(new Task()).when(taskRepository).save(any());

        assertThat(tasks.getOrCreate("TestTask1")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask2")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask3")).isNotNull();
    }

    @Test
    public void updateExistingTask() {
        Task existingTask = new Task("MyTitle");
        existingTask.setId(42L);
        existingTask.setDateCreation(Instant.now());
        existingTask.setDescription("MyTaskDescription");

        doReturn(Optional.of(existingTask)).when(taskRepository).findById(anyLong());
        doReturn(existingTask).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);
        taskEdit.setDescription("MyDescriptionUpdated");
        taskEdit.setTitle("MyTitleUpdated");

        try {
            Task updatedTask = tasks.update(taskEdit);
            assertThat(updatedTask.getTitle()).isEqualTo("MyTitleUpdated");
            assertThat(updatedTask.getDescription()).isEqualTo("MyDescriptionUpdated");
            assertThat(updatedTask.getDateClosed()).isNull();
        }
        catch (Throwable ignored) {
            fail("Failed to update a task");
        }
    }

    @Test
    public void closeTask() {
        Task existingTask = new Task("MyTitle");
        existingTask.setId(42L);

        doReturn(Optional.of(existingTask)).when(taskRepository).findById(anyLong());
        doReturn(existingTask).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);
        taskEdit.setClosed(true);

        try {
            Task updatedTask = tasks.update(taskEdit);
            assertThat(updatedTask.getDateClosed()).isNotNull();
        }
        catch (Throwable ignored) {
            fail("Failed to update a task");
        }
    }

    @Test
    public void updateNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);

        try {
            Task updatedTask = tasks.update(taskEdit);
            fail("Failed to detect a non-existing task during update.");
        }
        catch (Throwable ignored) {
        }
    }

    @Test
    public void updateTaskWithInvalidID() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        ReqTaskEdit taskEdit = new ReqTaskEdit();

        try {
            Task updatedTask = tasks.update(taskEdit);
            fail("Failed to detect a non-valid task ID during update.");
        }
        catch (Throwable ignored) {
        }
    }

    @Test
    public void deleteExistingTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        try {
            tasks.delete(42L);
        }
        catch (Throwable ignored) {
            fail("Failed to delete a task");
        }
    }

    @Test
    public void deleteNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        try {
            tasks.delete(42L);
            fail("Failed to detect non-existing task during deletion");
        }
        catch (Throwable ignored) {
        }
    }
}
