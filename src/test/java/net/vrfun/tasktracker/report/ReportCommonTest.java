/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.mockito.internal.util.collections.Sets;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;


public class ReportCommonTest {

    @NonNull
    protected ReqReportMailConfiguration createReqReportMailConfiguration(long id) {
        ReqReportMailConfiguration reqReportMailConfiguration = new ReqReportMailConfiguration();
        reqReportMailConfiguration.setId(id);
        reqReportMailConfiguration.setName("Name");
        reqReportMailConfiguration.setMailSenderName("SenderName");
        reqReportMailConfiguration.setMailSubject("Subject");
        reqReportMailConfiguration.setMailText("Text");
        reqReportMailConfiguration.setReportHour(18L);
        reqReportMailConfiguration.setReportMinute(0L);
        reqReportMailConfiguration.setReportPeriod("PERIOD_WEEKLY");
        reqReportMailConfiguration.setReportWeekDay("WEEKDAY_FRIDAY");
        reqReportMailConfiguration.setReportTitle("Report Title");
        reqReportMailConfiguration.setReportSubTitle("Report Sub-Title");
        reqReportMailConfiguration.setAdditionalRecipients(Sets.newSet(100L, 200L));
        reqReportMailConfiguration.setReportingTeams(Sets.newSet(500L));
        reqReportMailConfiguration.setReportToTeamLeads(true);
        reqReportMailConfiguration.setReportToTeamMembers(true);

        return reqReportMailConfiguration;
    }

    @NonNull
    protected List<Team> createTeams(@NonNull final List<Long> teamIDs) {
        List<Team> teams = new ArrayList<>();
        teamIDs.forEach((id) -> {
            Team team = new Team("Name " + id, "Description " + id);
            team.setId(id);
            teams.add(team);
        });
        return teams;
    }

    @NonNull
    protected List<Task> createTasks(@NonNull final List<Long> taskIDs) {
        List<Task> tasks = new ArrayList<>();
        taskIDs.forEach((id) -> {
            Task task = new Task("Title " + id);
            task.setDescription("Description " + id);
            task.setId(id);
            tasks.add(task);
        });
        return tasks;
    }
}
