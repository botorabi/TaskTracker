/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Collection;

/**
 * User's data transfer object
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class UserDTO {

    private long id;
    private String realName;
    private String login;
    private String email;
    private Instant dateCreation;
    private Instant lastLogin;
    private Collection<String> roles;

    public UserDTO() {}

    public UserDTO(@NonNull final User user) {
        this.id = user.getId();
        this.realName = user.getRealName();
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.dateCreation = user.getDateCreation();
        this.lastLogin = user.getLastLogin();
        this.roles = Role.getRolesAsString(user.getRoles());
    }

    public UserDTO(long id,
                   final String realName,
                   final String login,
                   final String email,
                   final Instant dateCreation,
                   final Instant lastLogin) {

        this.id = id;
        this.realName = realName;
        this.login = login;
        this.email = email;
        this.dateCreation = dateCreation;
        this.lastLogin = lastLogin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }
}
