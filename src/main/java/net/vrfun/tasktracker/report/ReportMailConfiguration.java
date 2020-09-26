/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.common.BaseEntity;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class ReportMailConfiguration extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String mailSenderName;

    @Column(nullable=false)
    private String mailSubject;

    @Column(length = 1024)
    private String mailText;

    @ManyToMany(targetEntity = Team.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<Team> reportingTeams;

    @ManyToMany(targetEntity = User.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<User> masterRecipients;

    @Column(nullable=false)
    private Boolean reportToTeamLeads = true;

    @Column(nullable=false)
    private Boolean reportToTeamMembers = true;

    @Enumerated(EnumType.STRING)
    private ReportPeriod reportPeriod = ReportPeriod.PERIOD_WEEKLY;

    @Enumerated(EnumType.STRING)
    private ReportWeekDay reportWeekDay = ReportWeekDay.WEEKDAY_FRIDAY;

    private Long reportHour = 18L;

    private Long reportMinute = 0L;


    public ReportMailConfiguration() {}

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull final Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getMailSenderName() {
        return mailSenderName;
    }

    public void setMailSenderName(@NonNull final String mailSenderName) {
        this.mailSenderName = mailSenderName;
    }

    @NonNull
    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(@NonNull final String mailSubject) {
        this.mailSubject = mailSubject;
    }

    @Nullable
    public String getMailText() {
        return mailText;
    }

    public void setMailText(@Nullable final String mailText) {
        this.mailText = mailText;
    }

    @Nullable
    public Collection<Team> getReportingTeams() {
        return reportingTeams;
    }

    public void setReportingTeams(@Nullable Collection<Team> reportingTeams) {
        this.reportingTeams = reportingTeams;
    }

    @Nullable
    public Collection<User> getMasterRecipients() {
        return masterRecipients;
    }

    public void setMasterRecipients(@Nullable final Collection<User> masterRecipients) {
        this.masterRecipients = masterRecipients;
    }

    @NonNull
    public Boolean getReportToTeamLeads() {
        return reportToTeamLeads;
    }

    public void setReportToTeamLeads(@NonNull final Boolean reportToTeamLeads) {
        this.reportToTeamLeads = reportToTeamLeads;
    }

    @NonNull
    public Boolean getReportToTeamMembers() {
        return reportToTeamMembers;
    }

    public void setReportToTeamMembers(@NonNull final Boolean reportToTeamMembers) {
        this.reportToTeamMembers = reportToTeamMembers;
    }

    @NonNull
    public ReportPeriod getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(@NonNull final ReportPeriod reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    @NonNull
    public ReportWeekDay getReportWeekDay() {
        return reportWeekDay;
    }

    public void setReportWeekDay(@NonNull final ReportWeekDay reportWeekDay) {
        this.reportWeekDay = reportWeekDay;
    }

    @NonNull
    public Long getReportHour() {
        return reportHour;
    }

    public void setReportHour(@NonNull final Long reportHour) {
        this.reportHour = reportHour;
    }

    @NonNull
    public Long getReportMinute() {
        return reportMinute;
    }

    public void setReportMinute(@NonNull final Long reportMinute) {
        this.reportMinute = reportMinute;
    }
}
