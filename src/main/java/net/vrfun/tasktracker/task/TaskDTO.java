/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.*;

/**
 * Task's data transfer object
 *
 * @author          boto
 * Creation Date    August 2020
 */
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Instant dateCreation;
    private Instant dateClosed;
    private Collection<Long> users;
    private Collection<Long> teams;
    private Collection<String> teamNames;

    public TaskDTO() {}

    public TaskDTO(@NonNull final Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dateCreation = task.getDateCreation();
        this.dateClosed = task.getDateClosed();
        if (task.getUsers() != null) {
            this.users = new ArrayList<>();
            task.getUsers().stream().forEach((user) -> this.users.add(user.getId()));
        }
        if (task.getTeams() != null) {
            this.teams = new ArrayList<>();
            this.setTeamNames(new ArrayList<>());
            task.getTeams().stream().forEach((team) -> {
                this.teams.add(team.getId());
                this.getTeamNames().add(team.getName());
            });
        }
    }

    public TaskDTO(long id,
                   final String title,
                   final String description,
                   final Instant dateCreation,
                   final Instant dateClosed,
                   final Collection<Long> users,
                   final Collection<Long> teams) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateClosed = dateClosed;
        this.users = users;
        this.teams = teams;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Instant dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Collection<Long> getUsers() {
        return users;
    }

    public void setUsers(Collection<Long> users) {
        this.users = users;
    }


    public Collection<Long> getTeams() {
        return teams;
    }

    public void setTeams(Collection<Long> teams) {
        this.teams = teams;
    }

    public Collection<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(Collection<String> teamNames) {
        this.teamNames = teamNames;
    }
}
