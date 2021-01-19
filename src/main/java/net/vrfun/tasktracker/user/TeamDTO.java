/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Team's data transfer object
 *
 * @author          boto
 * Creation Date    August 2020
 */
public class TeamDTO {

    private long id;
    private String name;
    private String description;
    private Collection<Long> userIDs;
    private Collection<Long> teamLeaderIDs;
    private Collection<String> teamLeaderNames;
    private boolean active;

    public TeamDTO() {}

    public TeamDTO(@NonNull final Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.active = team.getActive();
        if (team.getUsers() != null) {
            this.userIDs = team.getUsers().stream().
                map((user) -> user.getId())
                .collect(Collectors.toList());
        }
        if (team.getTeamLeaders() != null) {
            this.teamLeaderIDs = new ArrayList<>();
            this.teamLeaderNames = new ArrayList<>();
            team.getTeamLeaders().stream().forEach((teamLeader) -> {
                this.teamLeaderIDs.add(teamLeader.getId());
                this.teamLeaderNames.add(teamLeader.getRealName());
            });
        }
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

    public Collection<Long> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(Collection<Long> userIDs) {
        this.userIDs = userIDs;
    }

    public Collection<Long> getTeamLeaderIDs() {
        return teamLeaderIDs;
    }

    public void setTeamLeaderIDs(Collection<Long> teamLeaderIDs) {
        this.teamLeaderIDs = teamLeaderIDs;
    }

    public Collection<String> getTeamLeaderNames() {
        return teamLeaderNames;
    }

    public void setTeamLeaderNames(Collection<String> teamLeaderNames) {
        this.teamLeaderNames = teamLeaderNames;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
