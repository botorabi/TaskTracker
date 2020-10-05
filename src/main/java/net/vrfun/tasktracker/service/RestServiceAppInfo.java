/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


/**
 * REST API for retrieving application information
 *
 * @author          boto
 * Creation Date    October 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceAppInfo {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Autowired
    public RestServiceAppInfo() {
    }

    @GetMapping(value = "/app/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAppInformation() {
        final String appInfo = "{ \"name\" : \"" + appName + "\", \"version\": \"" + appVersion + "\" }";
        return new ResponseEntity<>(appInfo, HttpStatus.OK);
    }
}
