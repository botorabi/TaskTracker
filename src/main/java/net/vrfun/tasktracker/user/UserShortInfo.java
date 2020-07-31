/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.*;

import java.time.Instant;
import java.util.*;

/**
 * User's short info.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class UserShortInfo {

    private long id;

    private String realName;

    private String login;

    private Instant creationDate;

    private Instant lastLogin;

    private Collection<String> roles;

    public UserShortInfo() {}

    public UserShortInfo(@NonNull final User user) {
        this.id = user.getId();
        this.realName = user.getRealName();
        this.login = user.getLogin();
        this.creationDate = user.getCreationDate();
        this.lastLogin = user.getLastLogin();
        this.roles = Role.getRolesAsString(user.getRoles());
    }

    public UserShortInfo(long id,
                         final String realName,
                         final String login,
                         final Instant creationDate,
                         final Instant lastLogin) {

        this.id = id;
        this.realName = realName;
        this.login = login;
        this.creationDate = creationDate;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
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
