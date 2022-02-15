/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for user login.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class ReqLogin {

    private String login;

    private String password;

    public ReqLogin() {}

    public String getLogin() {
        return login;
    }

    @JsonProperty("login")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
}
