/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
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
 * Request for editing an existing or creating a new user.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class ReqUserEdit {

    private long id;

    private String realName;

    private String login;

    private String email;

    private String password;

    private Set<String> roles;


    public ReqUserEdit() {}

    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    @JsonProperty("realName")
    public void setRealName(@Nullable final String realName) {
        this.realName = realName;
    }

    public String getLogin() {
        return login;
    }

    @JsonProperty("login")
    public void setLogin(@Nullable final String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(@Nullable final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(@Nullable final String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(@Nullable Set<String> roles) {
        this.roles = roles;
    }
}
