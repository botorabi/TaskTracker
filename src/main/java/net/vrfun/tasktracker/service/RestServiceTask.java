/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Task related REST services
 *
 * @author          boto
 * Creation Date    July 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceTask {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Tasks tasks;

    @Autowired
    public RestServiceTask(@NonNull final Tasks tasks) {
        this.tasks = tasks;
    }

    @PostMapping("/task/create")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Long> create(@RequestBody ReqTaskEdit taskEdit) {
        try {
            return new ResponseEntity<>(tasks.create(taskEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create new task, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/task/edit")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Long> edit(@RequestBody ReqTaskEdit taskEdit) {
        try {
            return new ResponseEntity<>(tasks.update(taskEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not update task, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/task/delete/{id}")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            tasks.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete task, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/task")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return new ResponseEntity<>(tasks.getTasks(), HttpStatus.OK);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(tasks.getTaskById(id), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/task/search/{filter}")
    public ResponseEntity<List<TaskDTO>> searchTasks(@PathVariable("filter") String filter) {
        return new ResponseEntity<>(tasks.searchTasks(filter), HttpStatus.OK);
    }
}
