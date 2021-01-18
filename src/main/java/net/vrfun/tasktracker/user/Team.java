/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import net.vrfun.tasktracker.common.BaseEntity;
import org.springframework.lang.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class Team extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(length = 1024)
    private String description;

    private boolean active;

    @ManyToMany(targetEntity = User.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<User> users;

    @ManyToMany(targetEntity = User.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<User> teamLeaders;

    public Team() {}

    public Team(@NonNull final String name,
                @Nullable final String description) {

        this.name = name;
        this.description = description;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Nullable
    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(@Nullable Collection<User> users) {
        this.users = users;
    }

    @Nullable
    public Collection<User> getTeamLeaders() {
        return teamLeaders;
    }

    public void setTeamLeaders(@Nullable Collection<User> teamLeaders) {
        this.teamLeaders = teamLeaders;
    }
}
