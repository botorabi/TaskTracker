/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

/**
 * Utilities for handling with Tasks
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Service
public class Tasks {

    private TaskRepository taskRepository;

    @Autowired
    public Tasks(@NonNull final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @NonNull
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        taskRepository.findAll().forEach(tasks::add);
        return tasks;
    }

    @Nullable
    public Task get(long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.isPresent() ? task.get() : null;
    }

    @NonNull
    public Task create(@NonNull final ReqTaskEdit taskEdit) throws IllegalArgumentException {
        if (StringUtils.isEmpty(taskEdit.getTitle())) {
            throw new IllegalArgumentException("Missing task title");
        }
        Optional<Task> task = taskRepository.findTaskByTitle(taskEdit.getTitle());
        if (task.isPresent()) {
            throw new IllegalArgumentException("A task with this title already exists.");
        }
        Task newTask = new Task(taskEdit.getTitle());
        newTask.setDescription(taskEdit.getDescription());
        return taskRepository.save(newTask);
    }

    @NonNull
    public Task getOrCreate(@NonNull final String title) {
        Optional<Task> task = taskRepository.findTaskByTitle(title);
        if (task.isPresent()) {
            return task.get();
        }
        else {
            return taskRepository.save(new Task(title));
        }
    }

    @NonNull
    public Task update(@NonNull final ReqTaskEdit taskEdit) throws IllegalArgumentException {
        if (taskEdit.getId() == 0L) {
            throw new IllegalArgumentException("Invalid task ID.");
        }

        Optional<Task> task = taskRepository.findById(taskEdit.getId());
        if (!task.isPresent()) {
            throw new IllegalArgumentException("Task does not exists.");
        }
        task.get().setTitle(taskEdit.getTitle());
        task.get().setDescription(taskEdit.getDescription());
        if (taskEdit.isClosed()) {
            task.get().setDateClosed(Instant.now());
        }
        return taskRepository.save(task.get());
    }

    public void delete(long id) throws IllegalArgumentException {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.delete(task.get());
        }
        else {
            throw new IllegalArgumentException("Task does not exists.");
        }
    }
}
