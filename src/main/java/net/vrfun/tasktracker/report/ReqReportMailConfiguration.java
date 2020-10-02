/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Request for editing an existing or creating a new report generation configuration.
 *
 * @author          boto
 * Creation Date    September 2020
 */
public class ReqReportMailConfiguration {

    private long id;
    private String name;
    private String mailSenderName;
    private String mailSubject;
    private String mailText;
    private Set<Long> reportingTeams;
    private Set<Long> masterRecipients;
    private Boolean reportToTeamLeads;
    private Boolean reportToTeamMembers;
    private String reportPeriod;
    private String reportWeekDay;
    private Long reportHour;
    private Long reportMinute;
    private String reportTitle;
    private String reportSubTitle;

    public ReqReportMailConfiguration() {}

    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getMailSenderName() {
        return mailSenderName;
    }

    @JsonProperty("mailSenderName")
    public void setMailSenderName(String mailSenderName) {
        this.mailSenderName = mailSenderName;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    @JsonProperty("mailSubject")
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailText() {
        return mailText;
    }

    @JsonProperty("mailText")
    public void setMailText(String mailText) {
        this.mailText = mailText;
    }

    public Set<Long> getReportingTeams() {
        return reportingTeams;
    }

    @JsonProperty("reportingTeams")
    public void setReportingTeams(Set<Long> reportingTeams) {
        this.reportingTeams = reportingTeams;
    }

    public Set<Long> getMasterRecipients() {
        return masterRecipients;
    }

    @JsonProperty("masterRecipients")
    public void setMasterRecipients(Set<Long> masterRecipients) {
        this.masterRecipients = masterRecipients;
    }

    public Boolean getReportToTeamLeads() {
        return reportToTeamLeads;
    }

    @JsonProperty("reportToTeamLeads")
    public void setReportToTeamLeads(Boolean reportToTeamLeads) {
        this.reportToTeamLeads = reportToTeamLeads;
    }

    public Boolean getReportToTeamMembers() {
        return reportToTeamMembers;
    }

    @JsonProperty("reportToTeamMembers")
    public void setReportToTeamMembers(Boolean reportToTeamMembers) {
        this.reportToTeamMembers = reportToTeamMembers;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    @JsonProperty("reportPeriod")
    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getReportWeekDay() {
        return reportWeekDay;
    }

    @JsonProperty("reportWeekDay")
    public void setReportWeekDay(String reportWeekDay) {
        this.reportWeekDay = reportWeekDay;
    }

    public Long getReportHour() {
        return reportHour;
    }

    @JsonProperty("reportHour")
    public void setReportHour(Long reportHour) {
        this.reportHour = reportHour;
    }

    public Long getReportMinute() {
        return reportMinute;
    }

    @JsonProperty("reportMinute")
    public void setReportMinute(Long reportMinute) {
        this.reportMinute = reportMinute;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    @JsonProperty("reportTitle")
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportSubTitle() {
        return reportSubTitle;
    }

    @JsonProperty("reportSubTitle")
    public void setReportSubTitle(String reportSubTitle) {
        this.reportSubTitle = reportSubTitle;
    }
}
