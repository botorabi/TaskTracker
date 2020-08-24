/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.user.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;

    @Autowired
    public Tasks(@NonNull final TaskRepository taskRepository,
                 @NonNull final UserRepository userRepository,
                 @NonNull final TeamRepository teamRepository) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
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

        setTaskUsersAndTeams(newTask, taskEdit);

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

        setTaskUsersAndTeams(task.get(), taskEdit);

        return taskRepository.save(task.get());
    }

    private void setTaskUsersAndTeams(@NonNull final Task task, @NonNull final ReqTaskEdit taskEdit) {
        Collection<User> users = new ArrayList<>();
        Collection<Team> teams = new ArrayList<>();

        if (taskEdit.getUsers() != null) {
            taskEdit.getUsers().stream().forEach((userID) -> {
                Optional<User> user = userRepository.findById(userID);
                user.ifPresentOrElse(
                        (foundUser) -> users.add(user.get()),
                        () -> LOGGER.error("User with ID {} does not exist!", userID));
            });
        }

        if (taskEdit.getTeams() != null) {
            taskEdit.getTeams().stream().forEach((teamID) -> {
                Optional<Team> team = teamRepository.findById(teamID);
                team.ifPresentOrElse(
                        (foundTeam) -> teams.add(team.get()),
                        () -> LOGGER.error("Team with ID {} does not exist!", teamID));
            });
        }

        task.setUsers(users);
        task.setTeams(teams);
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

    @NonNull
    public List<TaskShortInfo> getTasks() {
        List<TaskShortInfo> tasks = new ArrayList<>();
        Iterable<Task> allTasks = taskRepository.findAll();
        if (allTasks != null) {
            allTasks.forEach((task) -> {
                TaskShortInfo t = new TaskShortInfo(task);
                tasks.add(t);
            });
        }
        return tasks;
    }

    @NonNull
    public TaskShortInfo getTaskById(Long id) throws IllegalArgumentException {
        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isEmpty()) {
            throw new IllegalArgumentException("Task with ID '" + id + "' does not exist!");
        }
        return new TaskShortInfo(foundTask.get());
    }

    @NonNull
    public List<TaskShortInfo> searchTasks(@NonNull final String filter) {
        return taskRepository.searchTask(filter);
    }
}
