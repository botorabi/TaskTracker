/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.Role;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Task progress related REST services
 *
 * @author          boto
 * Creation Date    August 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceProgress {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Progresses progresses;

    @Autowired
    public RestServiceProgress(@NonNull final Progresses progresses) {
        this.progresses = progresses;
    }

    @PostMapping("/progress/create")
    public ResponseEntity<Long> create(@RequestBody final ReqProgressEdit reqProgressEdit) {
        try {
            return new ResponseEntity<>(progresses.create(reqProgressEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create progress, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/progress/edit")
    public ResponseEntity<Long> edit(@RequestBody ReqProgressEdit reqProgressEdit) {
        try {
            return new ResponseEntity<>(progresses.editProgress(reqProgressEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not edit progress entry, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/progress/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long progressID) {
        try {
            progresses.delete(progressID);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete progress, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/progress/all")
    @Secured({Role.ROLE_NAME_ADMIN})
    public ResponseEntity<List<ProgressShortInfo>> getProgressAll() {
        return new ResponseEntity<>(progresses.getAll(), HttpStatus.OK);
    }

    @GetMapping("/progress/team/{id}")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<List<ProgressShortInfo>> getTeamProgress(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(progresses.getTeamProgress(id), HttpStatus.OK);
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ProgressShortInfo>> getUserProgress() {
        return new ResponseEntity<>(progresses.getUserProgress(), HttpStatus.OK);
    }

    @GetMapping("/progress/{id}")
    public ResponseEntity<ProgressShortInfo> getProgress(@PathVariable("id") final Long id) {
        ProgressShortInfo progress = progresses.get(id);
        if (progress == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(progress, HttpStatus.OK);
    }
}
