/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.task.Tag;
import net.vrfun.tasktracker.task.Tags;
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
 * Tag related REST services
 *
 * @author          boto
 * Creation Date    July 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceTag {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Tags tags;

    @Autowired
    public RestServiceTag(@NonNull final Tags tags) {
        this.tags = tags;
    }

    @PostMapping("/tag/create/{name}")
    public ResponseEntity<Long> getOrCreate(@PathVariable("name") final String name) {
        return new ResponseEntity<>(tags.getOrCreate(name).getId(), HttpStatus.OK);
    }

    @DeleteMapping("/tag/{name}")
    @Secured({Role.ROLE_NAME_ADMIN})
    public ResponseEntity<Void> delete(@PathVariable("name") final String name) {
        try {
            tags.delete(name);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete tag, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<List<Tag>> getAllTags() {
        return new ResponseEntity<>(tags.getAll(), HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable("id") final Long id) {
        Tag tag = tags.get(id);
        if (tag == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @GetMapping("/tag/find/{name}")
    public ResponseEntity<List<Tag>> getTag(@PathVariable("name") final String name) {
        return new ResponseEntity<>(tags.getSimilarTags(name), HttpStatus.OK);
    }
}
