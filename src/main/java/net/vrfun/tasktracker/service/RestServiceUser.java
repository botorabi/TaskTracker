/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.service.comm.*;
import net.vrfun.tasktracker.user.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User related REST services
 *
 * @author          boto
 * Creation Date    July 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceUser {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final UserAuthenticator userAuthenticator;

    private final Users users;


    @Autowired
    public RestServiceUser(@NonNull UserAuthenticator userAuthenticator,
                           @NonNull Users users) {

        this.userAuthenticator = userAuthenticator;
        this.users = users;
    }

    @PostMapping("/user/create")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Long> create(@RequestBody ReqUserEdit userCreate) {
        try {
            return new ResponseEntity<>(users.createUser(userCreate).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create new user, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/user/edit")
    public ResponseEntity<Long> edit(@RequestBody ReqUserEdit userEdit) {
        try {
            if (StringUtils.isEmpty(userEdit.getLogin())) {
                LOGGER.info("Could not edit user, missing login name!");
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            if (userAuthenticator.getUserLogin().equals(userEdit.getLogin())) {
                // users are not allowed to change their own roles
                userEdit.setRoles(null);
                return new ResponseEntity<>(users.editUser(userEdit).getId(), HttpStatus.OK);
            }
            else if (userAuthenticator.isRoleAdmin()) {
                return new ResponseEntity<>(users.editUser(userEdit).getId(), HttpStatus.OK);
            }
            else {
                LOGGER.info("Could not edit user, missing privilege!");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not edit user, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/user/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            users.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete user, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserShortInfo>> getUsers() {
        try {
            if (userAuthenticator.isRoleAdmin()) {
                return new ResponseEntity<>(users.getUsers(), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(
                        Arrays.asList(users.getUserByLogin(userAuthenticator.getUserLogin())), HttpStatus.OK);
            }
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get users, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserShortInfo> getUser(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(users.getUserById(id), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get user, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<RespAuthenticationStatus> loginUser(@RequestBody ReqLogin reqLogin) {
        if (userAuthenticator.isUserAuthenticated()) {
            userAuthenticator.logoutUser();
        }

        if (userAuthenticator.loginLocalUser(reqLogin.getLogin(), reqLogin.getPassword())) {
            LOGGER.info("Local user {} successfully logged in", reqLogin.getLogin());
        }
        else if (userAuthenticator.loginLDAPUser(reqLogin.getLogin(), reqLogin.getPassword())) {
            LOGGER.info("LDAP user {} successfully logged in", reqLogin.getLogin());
            LOGGER.info("  Creating a local user for ", reqLogin.getLogin());
            users.getOrCreateLocalUserFromLdap(reqLogin);
            if (!userAuthenticator.loginLocalUser(reqLogin.getLogin(), reqLogin.getPassword())) {
                LOGGER.error("  Failed to login local user {}!", reqLogin.getLogin());
            }
        }
        else {
            LOGGER.info("User login failed, {}", reqLogin.getLogin());
        }

        return createAuthenticationStatusResponse();
    }

    @GetMapping("/user/logout")
    public ResponseEntity<RespAuthenticationStatus> logoutUser() {
        userAuthenticator.logoutUser();
        return createAuthenticationStatusResponse();
    }

    @GetMapping("/user/availableroles")
    public ResponseEntity<Collection<String>> getAvailableRoles() {
        return new ResponseEntity<>(Role.getAllRolesAsString(), HttpStatus.OK);
    }

    @GetMapping("/user/status")
    public ResponseEntity<RespAuthenticationStatus> status() {
        return createAuthenticationStatusResponse();
    }

    private ResponseEntity<RespAuthenticationStatus> createAuthenticationStatusResponse() {
        return new ResponseEntity<>(
                new RespAuthenticationStatus(
                        userAuthenticator.getUserId(),
                        userAuthenticator.getUserLogin(),
                        userAuthenticator.isUserAuthenticated(),
                        userAuthenticator.getUserRoles()), HttpStatus.OK);
    }
}
