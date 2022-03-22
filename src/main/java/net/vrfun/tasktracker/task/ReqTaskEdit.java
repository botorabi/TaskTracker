/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Request for editing or creating a task
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class ReqTaskEdit {

    private long id;
    private String title;
    private String description;
    private boolean closed;
    private Collection<Long> users;
    private Collection<Long> teams;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isClosed() {
        return closed;
    }

    @JsonProperty("close")
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Collection<Long> getUsers() {
        return users;
    }

    @JsonProperty("users")
    public void setUsers(Collection<Long> users) {
        this.users = users;
    }

    public Collection<Long> getTeams() {
        return teams;
    }

    @JsonProperty("teams")
    public void setTeams(Collection<Long> teams) {
        this.teams = teams;
    }
}
