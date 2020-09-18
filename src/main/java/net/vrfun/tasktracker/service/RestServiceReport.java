/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.report.Reports;
import net.vrfun.tasktracker.user.Role;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Report related REST services
 *
 * @author          boto
 * Creation Date    September 2020
 */
@RestController
@RequestMapping(value="/api")
public class RestServiceReport {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Reports reports;

    @Autowired
    public RestServiceReport(@NonNull final Reports reports) {
        this.reports = reports;
    }

    @GetMapping(value = "/report/team/{teamIDs}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<ByteArrayResource> createTeamReportText(@PathVariable("teamIDs") final String teamIDs) throws IOException {
        List<Long> ids = Arrays.asList(teamIDs.split(",")).stream()
                .map((idAsString) -> Long.valueOf(idAsString))
                .collect(Collectors.toList());

        return new ResponseEntity<>(reports.createTeamReportText(ids), HttpStatus.OK);
    }
}
