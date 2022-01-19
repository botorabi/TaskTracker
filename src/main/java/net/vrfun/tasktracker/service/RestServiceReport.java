/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.report.*;
import net.vrfun.tasktracker.report.docgen.ReportFormat;
import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.Role;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.id;


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

    private final Reports reports;
    private final ReportComposer reportComposer;
    private final UserAuthenticator userAuthenticator;

    @Autowired
    public RestServiceReport(@NonNull final Reports reports,
                             @NonNull final ReportComposer reportComposer,
                             @NonNull UserAuthenticator userAuthenticator) {

        this.reports = reports;
        this.userAuthenticator = userAuthenticator;
        this.reportComposer = reportComposer;
    }

    @PostMapping("/report/generator-configuration/create")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Long> createGeneratorConfiguration(@RequestBody ReqReportMailConfiguration reqReportMailConfiguration) {
        try {
            return new ResponseEntity<>(reports.createMailConfiguration(reqReportMailConfiguration).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create new report generator configuration, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/report/generator-configuration/edit")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Long> editGeneratorConfiguration(@RequestBody ReqReportMailConfiguration reqReportMailConfiguration) {
        try {
            return new ResponseEntity<>(reports.editMailConfiguration(reqReportMailConfiguration).getId(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not edit report generator configuration, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/report/generator-configuration/delete/{id}")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<Void> deleteGeneratorConfiguration(@PathVariable("id") Long id) {
        try {
            reports.deleteMailConfiguration(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not delete report generator configuration, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/report/generator-configuration")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<List<ReportMailConfigurationDTO>> getGeneratorConfigurations() {
        try {
            return new ResponseEntity<>(reports.getReportMailConfigurations(), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get report generation configurations, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/report/generator-configuration/{id}")
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<ReportMailConfigurationDTO> getGeneratorConfiguration(@PathVariable("id") final Long configurationID) {
        try {
            return new ResponseEntity<>(reports.getReportMailConfiguration(configurationID), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not get report generation configuration, reason: {}", throwable.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping(value = "/report/team/{teamIDs}/{fromDaysSinceEpoch}/{toDaysSinceEpoch}/{title}/{subTitle}/{language}/{sortType}",
                produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD})
    public ResponseEntity<ByteArrayResource> createTeamReport(@PathVariable("teamIDs")            final String teamIDs,
                                                              @PathVariable("fromDaysSinceEpoch") final String fromDate,
                                                              @PathVariable("toDaysSinceEpoch")   final String toDate,
                                                              @PathVariable("title")              final String title,
                                                              @PathVariable("subTitle")           final String subTitle,
                                                              @PathVariable("language")           final String language,
                                                              @PathVariable("sortType")           final String sortType) {

        List<Long> ids = Arrays.stream(teamIDs.split(","))
                .map((val) -> Long.valueOf(val.trim()))
                .collect(Collectors.toList());

        if (!reports.validateUserAccess(ids)) {
            LOGGER.warn("Non authorized access of user {} to team reports", userAuthenticator.getUserLogin());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        LocalDate fromInDaysSinceEpoch = LocalDate.ofEpochDay(Integer.parseInt(fromDate));
        LocalDate toInDaysSinceEpoch   = LocalDate.ofEpochDay(Integer.parseInt(toDate));

        try (ByteArrayOutputStream report =
                     reportComposer.createTeamReport(
                             ids, fromInDaysSinceEpoch, toInDaysSinceEpoch,
                             ReportFormat.PDF,
                             StringUtils.isEmpty(title) ? "<Title>" : title,
                             StringUtils.isEmpty(subTitle) ? "<Sub-Title>" : subTitle,
                             StringUtils.isEmpty(language) ? "en" : language,
                             sortType)) {
            return new ResponseEntity<>(new ByteArrayResource(report.toByteArray()), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create report, reason: {}", throwable.getMessage());
            throwable.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping(value = "/report/user/{userId}/{fromDaysSinceEpoch}/{toDaysSinceEpoch}/{title}/{subTitle}/{language}/{sortType}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    @Secured({Role.ROLE_NAME_ADMIN, Role.ROLE_NAME_TEAM_LEAD, Role.ROLE_NAME_AUTHOR})
    public ResponseEntity<ByteArrayResource> createUserReport(@PathVariable("userId")             final String userId,
                                                              @PathVariable("fromDaysSinceEpoch") final String fromDate,
                                                              @PathVariable("toDaysSinceEpoch")   final String toDate,
                                                              @PathVariable("title")              final String title,
                                                              @PathVariable("subTitle")           final String subTitle,
                                                              @PathVariable("language")           final String language,
                                                              @PathVariable("sortType")           final String sortType) {
        Long id;
        try {
             id = Long.valueOf(userId);
        }
        catch (NumberFormatException e)
        {
            LOGGER.info("Invalid user id: {}", e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        if (!reports.validateUserId(id)) {
            LOGGER.warn("Non authorized access of user {} ", userAuthenticator.getUserLogin());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        LocalDate fromInDaysSinceEpoch = LocalDate.ofEpochDay(Integer.parseInt(fromDate));
        LocalDate toInDaysSinceEpoch   = LocalDate.ofEpochDay(Integer.parseInt(toDate));

        try {
            ByteArrayOutputStream report =
                    reportComposer.createUserReport(
                            id, fromInDaysSinceEpoch, toInDaysSinceEpoch,
                            ReportFormat.PDF,
                            StringUtils.isEmpty(title) ? "<Title>" : title,
                            StringUtils.isEmpty(subTitle) ? "<Sub-Title>" : subTitle,
                            StringUtils.isEmpty(language) ? "en" : language,
                            sortType);
            return new ResponseEntity<>(new ByteArrayResource(report.toByteArray()), HttpStatus.OK);
        }
        catch(Throwable throwable) {
            LOGGER.info("Could not create report, reason: {}", throwable.getMessage());
            throwable.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
