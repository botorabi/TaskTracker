/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReportSectionGeneratorTest {

    private static boolean compareWith(List<String> testNames, List<Progress> progresses, ReportSortType type) {
        var reportSectionGenerator = ReportSectionGenerator.build(progresses)
                                                            .sortBy(type);
        List<ReportSection> sections =  reportSectionGenerator.create();
        List<String> sectionsNames = sections.stream().map(ReportSection::getSectionTitle).collect(Collectors.toList());

        Collections.sort(testNames);
        return sectionsNames.equals(testNames);
    }

    private static Progress getVanillaProgress()
    {
        Progress progress = new Progress("OWNER", 1L);
        Team team = new Team("Team", "");
        Task task = new Task("task");
        task.setTeams(List.of(team));
        progress.setTask(task);
        Instant instant = Instant.now();
        LocalDate localdate = LocalDate.ofInstant(instant, ZoneId.of("UTC"));
        progress.setDateCreation(instant);
        progress.setReportWeek(localdate);
        progress.setText("");
        progress.setTitle("title");
        return progress;
    }

    @Test
    void getSectionsSortByTeam() {
        List<String> testNames = new ArrayList<>(Arrays.asList( "c", "b", "a"));
        Task task1 = new Task("task1");
        Task task2 = new Task("task2");

        List<Team> teams1 = new ArrayList<>();
        for(int i = 0; i < testNames.size() - 1 ; ++i) {
            teams1.add (new Team(testNames.get(i), ""));
        }
        List<Team> teams2 = new ArrayList<>();
        for(int i = 1; i < testNames.size(); ++i) {
            teams2.add (new Team(testNames.get(i), ""));
        }

        task1.setTeams(teams1);
        task2.setTeams(teams2);

        List<Progress> progresses = new ArrayList<>();
        Progress progress1 = getVanillaProgress();
        progress1.setTask(task1);
        progress1.setReportWeek(LocalDate.ofInstant(Instant.parse("1982-01-09T10:15:13.00Z"), ZoneId.of("UTC")));
        progresses.add(progress1);
        Progress progress2 = getVanillaProgress();
        progress2.setTask(task2);
        progress2.setReportWeek(LocalDate.ofInstant(Instant.parse("1982-01-09T10:15:13.00Z"), ZoneId.of("UTC")));
        progresses.add(progress2);

        assertThat(compareWith(testNames, progresses, ReportSortType.REPORT_SORT_TYPE_TEAM)).isTrue();
    }

    @Test
    void getSectionsSortByUser() {
        List<String> testNames = new ArrayList<>(Arrays.asList( "c", "b", "a"));
        List<Progress> progresses = new ArrayList<>();
        for (int i = 0; i < testNames.size(); ++i) {
            Progress progress = getVanillaProgress();
            progress.setOwnerName(testNames.get(i));
            progress.setOwnerId((long)i);
            progress.setReportWeek(LocalDate.ofInstant(Instant.parse("1982-01-09T10:15:13.00Z"), ZoneId.of("UTC")));
            progresses.add(progress);
        }
        assertThat(compareWith(testNames, progresses, ReportSortType.REPORT_SORT_TYPE_USER)).isTrue();
    }

    @Test
    void getSectionsSortByTask() {
        List<String> testNames = new ArrayList<>(Arrays.asList( "c", "b", "a"));
        List<Task> tasks = new ArrayList<>();
        for (String testName : testNames) {
            tasks.add(new Task(testName));
        }
        List<Progress> progresses = new ArrayList<>();
        for (Task task : tasks) {
            Progress progress = getVanillaProgress();
            progress.setTask(task);
            progress.setReportWeek(LocalDate.ofInstant(Instant.parse("1982-01-09T10:15:13.00Z"), ZoneId.of("UTC")));
            progresses.add(progress);
        }

        assertThat(compareWith(testNames, progresses, ReportSortType.REPORT_SORT_TYPE_TASK)).isTrue();
    }

    @Test
    void getSectionsSortByWeek() {
        List<String> testNames = new ArrayList<>(Arrays.asList(
                "1982-01-09T10:15:13.00Z",
                "1980-04-09T10:08:30.00Z",
                "1983-07-09T10:17:30.00Z"
        ));
        List<Instant> instants = new ArrayList<>();
        for (String testName : testNames) {
            instants.add(Instant.parse(testName));
        }
        List<Progress> progresses = new ArrayList<>();
        List<String> weekNames = new ArrayList<>();
        for (Instant instant : instants) {
            Progress progress = getVanillaProgress();
            progress.setDateCreation(instant);
            progress.setReportWeek(LocalDate.ofInstant(instant, ZoneId.of("UTC")));
            progresses.add(progress);
            String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                    progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            weekNames.add(yearWeek);
        }

        assertThat(compareWith(weekNames, progresses, ReportSortType.REPORT_SORT_TYPE_WEEK)).isTrue();
    }
}