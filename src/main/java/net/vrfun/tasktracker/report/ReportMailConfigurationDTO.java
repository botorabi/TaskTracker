/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.User;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Report generation configuration's data transfer object.
 *
 * @author          boto
 * Creation Date    September 2020
 */
public class ReportMailConfigurationDTO {

    private long id;
    private String name;
    private String language;
    private String mailSenderName;
    private String mailSubject;
    private String mailText;
    private Collection<Long> reportingTeams;
    private Collection<Long> additionalRecipients;
    private Boolean reportToTeamLeads;
    private Boolean reportToTeamMembers;
    private String reportPeriod;
    private String reportWeekDay;
    private Long reportHour;
    private Long reportMinute;
    private String reportTitle;
    private String reportSubTitle;

    public ReportMailConfigurationDTO(@NonNull final ReportMailConfiguration reportMailConfiguration) {
        this.id = reportMailConfiguration.getId();
        this.name = reportMailConfiguration.getName();
        this.language = reportMailConfiguration.getLanguage();
        this.mailSenderName = reportMailConfiguration.getMailSenderName();
        this.mailSubject = reportMailConfiguration.getMailSubject();
        this.mailText = reportMailConfiguration.getMailText();
        if (reportMailConfiguration.getReportingTeams() != null) {
            this.reportingTeams = reportMailConfiguration.getReportingTeams().stream()
                    .map((Team::getId))
                    .collect(Collectors.toList());
        }
        if (reportMailConfiguration.getAdditionalRecipients() != null) {
            this.additionalRecipients = reportMailConfiguration.getAdditionalRecipients().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
        }
        this.reportToTeamLeads = reportMailConfiguration.getReportToTeamLeads();
        this.reportToTeamMembers = reportMailConfiguration.getReportToTeamMembers();
        this.reportPeriod = reportMailConfiguration.getReportPeriod().name();
        this.reportWeekDay = reportMailConfiguration.getReportWeekDay().name();
        this.reportHour = reportMailConfiguration.getReportHour();
        this.reportMinute = reportMailConfiguration.getReportMinute();
        this.setReportTitle(reportMailConfiguration.getReportTitle());
        this.setReportSubTitle(reportMailConfiguration.getReportSubTitle());
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMailSenderName() {
        return mailSenderName;
    }

    public void setMailSenderName(String mailSenderName) {
        this.mailSenderName = mailSenderName;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailText() {
        return mailText;
    }

    public void setMailText(String mailText) {
        this.mailText = mailText;
    }

    public Collection<Long> getReportingTeams() {
        return reportingTeams;
    }

    public void setReportingTeams(Collection<Long> reportingTeams) {
        this.reportingTeams = reportingTeams;
    }

    public Collection<Long> getAdditionalRecipients() {
        return additionalRecipients;
    }

    public void setAdditionalRecipients(Collection<Long> additionalRecipients) {
        this.additionalRecipients = additionalRecipients;
    }

    public Boolean getReportToTeamLeads() {
        return reportToTeamLeads;
    }

    public void setReportToTeamLeads(Boolean reportToTeamLeads) {
        this.reportToTeamLeads = reportToTeamLeads;
    }

    public Boolean getReportToTeamMembers() {
        return reportToTeamMembers;
    }

    public void setReportToTeamMembers(Boolean reportToTeamMembers) {
        this.reportToTeamMembers = reportToTeamMembers;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getReportWeekDay() {
        return reportWeekDay;
    }

    public void setReportWeekDay(String reportWeekDay) {
        this.reportWeekDay = reportWeekDay;
    }

    public Long getReportHour() {
        return reportHour;
    }

    public void setReportHour(Long reportHour) {
        this.reportHour = reportHour;
    }

    public Long getReportMinute() {
        return reportMinute;
    }

    public void setReportMinute(Long reportMinute) {
        this.reportMinute = reportMinute;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportSubTitle() {
        return reportSubTitle;
    }

    public void setReportSubTitle(String reportSubTitle) {
        this.reportSubTitle = reportSubTitle;
    }
}
