/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Teams related REST services
 *
 * @author          boto
 * Creation Date    August 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceTeam {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Teams teams;

    private final UserAuthenticator userAuthenticator;


    @Autowired
    public RestServiceTeam(@NonNull final Teams teams,
                           @NonNull final UserAuthenticator userAuthenticator) {

        this.teams = teams;
        this.userAuthenticator = userAuthenticator;
    }

    @PostMapping("/team/create")
    @Secured({Role.ROLE_NAME_ADMIN})
    public ResponseEntity<Long> create(@RequestBody ReqTeamEdit reqTeamEdit) {
        try {
            return new ResponseEntity<>(teams.createTeam(reqTeamEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create new team, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/team/edit")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Long> edit(@RequestBody ReqTeamEdit reqTeamEdit) {
        try {
            // only the admin is allowed to change team's active flag
            if (!userAuthenticator.isRoleAdmin()) {
                reqTeamEdit.setActive(null);
            }
            return new ResponseEntity<>(teams.editTeam(reqTeamEdit).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not edit team, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/team/{id}")
    @Secured({Role.ROLE_NAME_ADMIN})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            teams.deleteTeam(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete team, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/team/search/{filter}")
    public ResponseEntity<List<TeamShortInfo>> searchTeam(@PathVariable("filter") String filter) {
        return new ResponseEntity<>(teams.searchTeams(filter), HttpStatus.OK);
    }

    @GetMapping("/team")
    public ResponseEntity<List<TeamShortInfo>> getTeams() {
        try {
            return new ResponseEntity<>(teams.getTeams(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get teams, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/team/{id}")
    public ResponseEntity<TeamShortInfo> getTeam(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(teams.getTeamById(id), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get team, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
