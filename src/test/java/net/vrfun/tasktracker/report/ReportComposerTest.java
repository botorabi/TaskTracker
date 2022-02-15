/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.report.docgen.ReportFormat;
import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReportComposerTest {

    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    private ReportComposer reportComposer;
    private ReportCommonTest reportCommonTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        reportComposer = new ReportComposer(
                progressRepository,
                teamRepository,
                taskRepository,
                userRepository);

        reportCommonTest = new ReportCommonTest();
    }

    @Test
    public void createTeamReportInvalidTeamsIDs() {
        doReturn(new ArrayList<>()).when(teamRepository).findAllById(anyList());

        LocalDate currentDate = LocalDate.now();
        LocalDate fromDate = currentDate.minusWeeks(1L);

        assertThatThrownBy(() -> reportComposer.createTeamReport(
                Arrays.asList(100L),
                fromDate,
                currentDate,
                ReportFormat.PDF,
                "Title",
                "SubTitle",
                "en",
                ReportSortType.REPORT_SORT_TYPE_TEAM.toString()));
    }

    @Test
    public void createTeamReportInvalidLanguage() {
        List<Team> teams = reportCommonTest.createTeams(Arrays.asList(10L, 20L));
        List<Task> tasks = reportCommonTest.createTasks(Arrays.asList(110L, 120L));

        doReturn(teams).when(teamRepository).findAllById(Arrays.asList(100L));
        doReturn(tasks).when(taskRepository).findTeamTasks(any());

        LocalDate currentDate = LocalDate.now();
        LocalDate fromDate = currentDate.minusWeeks(1L);

        assertThatThrownBy(() -> reportComposer.createTeamReport(
                Arrays.asList(100L),
                fromDate,
                currentDate,
                ReportFormat.PDF,
                "Title",
                "SubTitle",
                "INVALID-LANGUAGE",
                ReportSortType.REPORT_SORT_TYPE_TEAM.toString())).isNotNull();
    }

    @Test
    public void createTeamReport() {
        List<Team> teams = reportCommonTest.createTeams(Arrays.asList(10L, 20L));
        List<Task> tasks = reportCommonTest.createTasks(Arrays.asList(110L, 120L));

        doReturn(teams).when(teamRepository).findAllById(Arrays.asList(100L));
        doReturn(tasks).when(taskRepository).findTeamTasks(any());

        LocalDate currentDate = LocalDate.now();
        LocalDate fromDate = currentDate.minusWeeks(1L);

        assertThat(reportComposer.createTeamReport(
                Arrays.asList(100L),
                fromDate,
                currentDate,
                ReportFormat.PDF,
                "Title",
                "SubTitle",
                "en",
                ReportSortType.REPORT_SORT_TYPE_TEAM.toString())).isNotNull();
    }
}