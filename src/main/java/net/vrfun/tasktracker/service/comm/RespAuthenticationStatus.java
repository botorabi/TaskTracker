/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service.comm;

import net.vrfun.tasktracker.user.Role;

import java.util.Set;

/**
 * Response of user status.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class RespAuthenticationStatus {

    private long id;
    private String name;
    private boolean authenticated;
    private Set<Role> roles;

    public RespAuthenticationStatus() {}

    public RespAuthenticationStatus(long id,
                                    final String name,
                                    final boolean authenticated,
                                    final Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.authenticated = authenticated;
        this.roles = roles;
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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
