/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.report.docgen.ReportFormat;
import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)

public class ReportComposerTest {

    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TaskRepository taskRepository;

    private ReportComposer reportComposer;
    private ReportCommonTest reportCommonTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        reportComposer = new ReportComposer(
                progressRepository,
                teamRepository,
                taskRepository);

        reportCommonTest = new ReportCommonTest();
    }

    @Test
    public void createTeamReportCurrentWeekInvalidTeamsIDs() {
        doReturn(new ArrayList<>()).when(teamRepository).findAllById(anyList());

        assertThatThrownBy(() -> reportComposer.createTeamReportCurrentWeek(
                Arrays.asList(100L),
                ReportFormat.PDF,
                "Title",
                "SubTitle"));
    }

    @Test
    public void createTeamReportCurrentWeek() {
        List<Team> teams = reportCommonTest.createTeams(Arrays.asList(10L, 20L));
        List<Task> tasks = reportCommonTest.createTasks(Arrays.asList(110L, 120L));

        doReturn(teams).when(teamRepository).findAllById(Arrays.asList(100L));
        doReturn(tasks).when(taskRepository).findTeamTasks(any());

        assertThat(reportComposer.createTeamReportCurrentWeek(
                Arrays.asList(100L),
                ReportFormat.PDF,
                "Title",
                "SubTitle")).isNotNull();
    }
}