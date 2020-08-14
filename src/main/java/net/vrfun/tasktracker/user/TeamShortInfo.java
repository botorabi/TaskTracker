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
import java.util.*;

/**
 * Team's short info.
 *
 * @author          boto
 * Creation Date    August 2020
 */
public class TeamShortInfo {

    private long id;

    private String name;

    private String description;

    private Collection<Long> users;

    private boolean active;

    public TeamShortInfo() {}

    public TeamShortInfo(@NonNull final Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.active = team.getActive();
        if (team.getUsers() != null) {
            this.users = new ArrayList<>();
            team.getUsers().stream().forEach((user) -> this.users.add(user.getId()));
        }
    }

    public TeamShortInfo(long id,
                         final String name,
                         final String description,
                         final boolean active,
                         final Collection<Long> users) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Long> getUsers() {
        return users;
    }

    public void setUsers(Collection<Long> users) {
        this.users = users;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
