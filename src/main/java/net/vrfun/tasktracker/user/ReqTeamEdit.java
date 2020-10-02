/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Request for editing an existing or creating a new team.
 *
 * @author          boto
 * Creation Date    August 2020
 */
public class ReqTeamEdit {

    private long id;

    private String name;

    private String description;

    private Boolean active;

    private Set<Long> userIDs;

    private Set<Long> teamLeaderIDs;

    public ReqTeamEdit() {}

    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(@Nullable final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    @Nullable
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(@Nullable Boolean active) {
        this.active = active;
    }

    @Nullable
    public Set<Long> getUserIDs() {
        return userIDs;
    }

    @JsonProperty("userIDs")
    public void setUserIDs(@Nullable final Set<Long> userIDs) {
        this.userIDs = userIDs;
    }

    @Nullable
    public Set<Long> getTeamLeaderIDs() {
        return teamLeaderIDs;
    }

    @JsonProperty("teamLeaderIDs")
    public void setTeamLeaderIDs(@Nullable final Set<Long> teamLeaderIDs) {
        this.teamLeaderIDs = teamLeaderIDs;
    }
}
