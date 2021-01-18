/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for report generation
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
public class Reports {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ReportMailConfigurationRepository reportMailConfigurationRepository;
    private final ReportGeneratorScheduler reportGeneratorScheduler;
    private final UserAuthenticator userAuthenticator;


    @Autowired
    public Reports(@NonNull final UserRepository userRepository,
                   @NonNull final TeamRepository teamRepository,
                   @NonNull final ReportMailConfigurationRepository reportMailConfigurationRepository,
                   @NonNull final ReportGeneratorScheduler reportGeneratorScheduler,
                   @NonNull final UserAuthenticator userAuthenticator) {

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.reportMailConfigurationRepository = reportMailConfigurationRepository;
        this.reportGeneratorScheduler = reportGeneratorScheduler;
        this.userAuthenticator = userAuthenticator;
    }

    public boolean validateUserAccess(@NonNull final List<Long> teamIDs) {
        if (userAuthenticator.isRoleAdmin()) {
            return true;
        }

        List<Long> userTeamIDs = teamRepository.findTeamLeadTeams(userAuthenticator.getUser()).stream()
                .map((team) -> team.getId())
                .collect(Collectors.toList());

        return userTeamIDs.containsAll(teamIDs);
    }

    @NonNull
    public ReportMailConfiguration createMailConfiguration(@NonNull final ReqReportMailConfiguration reqReportMailConfiguration) {
        validateNewReportMailConfiguration(reqReportMailConfiguration);

        ReportMailConfiguration reportMailConfiguration = new ReportMailConfiguration();

        reportMailConfiguration.setName(reqReportMailConfiguration.getName());
        reportMailConfiguration.setMailSenderName(reqReportMailConfiguration.getMailSenderName());
        reportMailConfiguration.setMailSubject(reqReportMailConfiguration.getMailSubject());
        reportMailConfiguration.setMailText(reqReportMailConfiguration.getMailText());
        reportMailConfiguration.setReportToTeamLeads(reqReportMailConfiguration.getReportToTeamLeads());
        reportMailConfiguration.setReportToTeamMembers(reqReportMailConfiguration.getReportToTeamMembers());
        reportMailConfiguration.setReportPeriod(ReportPeriod.fromString(reqReportMailConfiguration.getReportPeriod()));
        reportMailConfiguration.setReportWeekDay(ReportWeekDay.fromString(reqReportMailConfiguration.getReportWeekDay()));
        reportMailConfiguration.setReportHour(reqReportMailConfiguration.getReportHour());
        reportMailConfiguration.setReportMinute(reqReportMailConfiguration.getReportMinute());
        reportMailConfiguration.setReportTitle(reqReportMailConfiguration.getReportTitle());
        reportMailConfiguration.setReportSubTitle(reqReportMailConfiguration.getReportSubTitle());

        setReportingTeamsAndAdditionalRecipients(reportMailConfiguration, reqReportMailConfiguration);

        ReportMailConfiguration newReportMailConfiguration = reportMailConfigurationRepository.save(reportMailConfiguration);

        reportGeneratorScheduler.addOrUpdateReportingJob(newReportMailConfiguration);

        return newReportMailConfiguration;
    }

    protected void validateNewReportMailConfiguration(@NonNull final ReqReportMailConfiguration reqReportMailConfiguration) {
        if (StringUtils.isEmpty(reqReportMailConfiguration.getName())) {
            throw new IllegalArgumentException("Missing Configuration Name");
        }
        if (StringUtils.isEmpty(reqReportMailConfiguration.getMailSenderName())) {
            throw new IllegalArgumentException("Missing Mail Sender Name");
        }
        if (StringUtils.isEmpty(reqReportMailConfiguration.getMailSubject())) {
            throw new IllegalArgumentException("Missing Mail Subject");
        }
        if (CollectionUtils.isEmpty(reqReportMailConfiguration.getReportingTeams())) {
            throw new IllegalArgumentException("Missing Reporting Teams");
        }
        if (ReportPeriod.fromString(reqReportMailConfiguration.getReportPeriod()) == ReportPeriod.PERIOD_UNKNOWN) {
            throw new IllegalArgumentException("Invalid Report Period");
        }
        if (ReportWeekDay.fromString(reqReportMailConfiguration.getReportWeekDay()) == ReportWeekDay.WEEKDAY_UNKNOWN) {
            throw new IllegalArgumentException("Invalid Report Week Day");
        }
        Long reportHour = reqReportMailConfiguration.getReportHour();
        if (reportHour == null || !(reportHour >= 0L && reportHour <= 24L)) {
            throw new IllegalArgumentException("Invalid Report Hour");
        }
        Long reportMinute = reqReportMailConfiguration.getReportMinute();
        if (reportMinute == null || !(reportMinute >= 0L && reportMinute <= 59L)) {
            throw new IllegalArgumentException("Invalid Report Minute");
        }
    }

    protected void setReportingTeamsAndAdditionalRecipients(@NonNull final ReportMailConfiguration reportMailConfiguration,
                                                            @NonNull final ReqReportMailConfiguration reqReportMailConfiguration) {

        List<Team> reportingTeams = new ArrayList<>();
        reqReportMailConfiguration.getReportingTeams().forEach((teamID) -> {
            Optional<Team> foundTeam = teamRepository.findById(teamID);
            foundTeam.ifPresentOrElse(
                    (team) -> reportingTeams.add(team),
                    () -> LOGGER.warn("Cannot setup Team with ID {} for reporting configuration, it does not exist!")
            );
        });
        reportMailConfiguration.setReportingTeams(reportingTeams);

        List<User> additionalRecipients = new ArrayList<>();
        reqReportMailConfiguration.getAdditionalRecipients().forEach((userID) -> {
            Optional<User> foundUser = userRepository.findById(userID);
            foundUser.ifPresentOrElse(
                    (user) -> additionalRecipients.add(user),
                    () -> LOGGER.warn("Cannot setup additional recipient user with ID {} for reporting configuration, it does not exist!")
            );
        });
        reportMailConfiguration.setAdditionalRecipients(additionalRecipients);
    }

    @NonNull
    public ReportMailConfiguration editMailConfiguration(@NonNull final ReqReportMailConfiguration reqReportMailConfiguration) {
        Optional<ReportMailConfiguration> config = reportMailConfigurationRepository.findById(reqReportMailConfiguration.getId());
        if (!config.isPresent()) {
            throw new IllegalArgumentException("Could not find report generation configuration with given ID");
        }

        if (!StringUtils.isEmpty(reqReportMailConfiguration.getLanguage())) {
            config.get().setLanguage(reqReportMailConfiguration.getLanguage());
        }
        if (!StringUtils.isEmpty(reqReportMailConfiguration.getMailSenderName())) {
            config.get().setMailSenderName(reqReportMailConfiguration.getMailSenderName());
        }
        if (!StringUtils.isEmpty(reqReportMailConfiguration.getMailSubject())) {
            config.get().setMailSubject(reqReportMailConfiguration.getMailSubject());
        }
        if (!StringUtils.isEmpty(reqReportMailConfiguration.getMailText())) {
            config.get().setMailText(reqReportMailConfiguration.getMailText());
        }
        if (reqReportMailConfiguration.getReportToTeamLeads() != null) {
            config.get().setReportToTeamLeads(reqReportMailConfiguration.getReportToTeamLeads());
        }
        if (reqReportMailConfiguration.getReportToTeamMembers() != null) {
            config.get().setReportToTeamMembers(reqReportMailConfiguration.getReportToTeamMembers());
        }

        if (reqReportMailConfiguration.getReportPeriod() != null) {
            config.get().setReportPeriod(ReportPeriod.valueOf(reqReportMailConfiguration.getReportPeriod()));
        }
        if (reqReportMailConfiguration.getReportWeekDay() != null) {
            config.get().setReportWeekDay(ReportWeekDay.valueOf(reqReportMailConfiguration.getReportWeekDay()));
        }
        if (reqReportMailConfiguration.getReportHour() != null) {
            config.get().setReportHour(reqReportMailConfiguration.getReportHour());
        }
        if (reqReportMailConfiguration.getReportMinute() != null) {
            config.get().setReportMinute(reqReportMailConfiguration.getReportMinute());
        }
        if (!StringUtils.isEmpty(reqReportMailConfiguration.getReportTitle())) {
            config.get().setReportTitle(reqReportMailConfiguration.getReportTitle());
        }
        if (!StringUtils.isEmpty(reqReportMailConfiguration.getReportSubTitle())) {
            config.get().setReportSubTitle(reqReportMailConfiguration.getReportSubTitle());
        }

        setReportingTeamsAndAdditionalRecipients(config.get(), reqReportMailConfiguration);

        reportGeneratorScheduler.addOrUpdateReportingJob(config.get());

        return reportMailConfigurationRepository.save(config.get());
    }

    public void deleteMailConfiguration(@NonNull final Long configurationID) {
        Optional<ReportMailConfiguration> configuration = Optional.empty();
        if (userAuthenticator.isRoleAdmin()) {
            configuration = reportMailConfigurationRepository.findById(configurationID);
        } else if (userAuthenticator.isRoleTeamLead()) {
            configuration = reportMailConfigurationRepository.findTeamLeadConfiguration(userAuthenticator.getUser(), configurationID);
        }

        if (configuration.isPresent()) {
            reportMailConfigurationRepository.delete(configuration.get());
            reportGeneratorScheduler.removeReportingJob(configurationID);
        }
        else {
            throw new IllegalArgumentException("Could not access report mail configuration for deletion");
        }
    }

    @NonNull
    public List<ReportMailConfigurationDTO> getReportMailConfigurations() {
        if (userAuthenticator.isRoleAdmin()) {
            return reportMailConfigurationRepository.findAll().stream()
                    .map((config) -> new ReportMailConfigurationDTO((config)))
                    .collect(Collectors.toList());
        } else if (userAuthenticator.isRoleTeamLead()) {
            return reportMailConfigurationRepository.findTeamLeadConfigurations(userAuthenticator.getUser()).stream()
                    .map((config) -> new ReportMailConfigurationDTO((config)))
                    .collect(Collectors.toList());
        }
        else {
            throw new IllegalArgumentException("Unauthorized access to report mail configurations");
        }
    }

    @NonNull
    public ReportMailConfigurationDTO getReportMailConfiguration(@NonNull final Long id) {
        Optional<ReportMailConfiguration> configuration;
        if (userAuthenticator.isRoleAdmin()) {
            configuration = reportMailConfigurationRepository.findById(id);
        } else if (userAuthenticator.isRoleTeamLead()) {
            configuration = reportMailConfigurationRepository.findTeamLeadConfiguration(userAuthenticator.getUser(), id);
        }
        else {
            throw new IllegalArgumentException("Unauthorized access to report mail configuration");
        }

        if (configuration.isPresent()) {
            return new ReportMailConfigurationDTO(configuration.get());
        } else {
            throw new IllegalArgumentException("Report mail configuration with ID " + id + " does not exist.");
        }
    }
}
