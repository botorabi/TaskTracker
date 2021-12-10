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
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    static private List<Progress> getEmptyProgresses(int amount) {
        List<Progress> progresses = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            progresses.add(new Progress());
        }
        return progresses;
    }

    @Test
    void emptyConstructor() {
        assertThat((new Report())).isNotNull();
    }

    @Test
    void progressConstructor() {
        assertThat((new Report(getEmptyProgresses(10).stream()))).isNotNull();
    }

    @Test
    void addProgress() {
        Report report = new Report();
        report.addProgress(getEmptyProgresses(10).stream());
    }

    @Test
    void resetProgress() {
        List<Progress> progresses = getEmptyProgresses(10);
        Report report = new Report();
        report.resetProgress(progresses.stream());
        Report report2 = new Report(progresses.stream());
        report2.resetProgress(progresses.stream());
    }

    @Test
    void multiAddProgress() {
        List<Progress> progresses = getEmptyProgresses(10);
        for(Progress progress : progresses) {
            progress.setOwnerName("Dummy");
        }
        Report report = new Report(progresses.stream());
        report.addProgress(progresses.stream());
        report.addProgress(progresses.stream());
        report.sortSectionsBy(ReportSortType.REPORT_SORT_TYPE_USER);
        assertThat(report.getSections()).isNotEmpty();
        assertThat(report.getSections().get(0).getSectionProgess().collect(Collectors.toList())).hasSize(progresses.size() * 3);
    }

    @Test
    void getSectionsEmpty() {
        Report report = new Report();
        assertThat(report.getSections()).isEmpty();
    }

    @Test
    void getSectionsUnsorted() {
        List<Progress> progresses = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            progresses.add(new Progress());
        }
        Report report = new Report(progresses.stream());
        assertThat(report.getSections()).isEmpty();
    }

    @Test
    void getSectionsEmptyTaskProgresses() {
        List<Progress> progresses = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            progresses.add(new Progress());
        }
        Report report = new Report(progresses.stream());
        report.sortSectionsBy(ReportSortType.REPORT_SORT_TYPE_TASK);
        assertThat(report.getSections()).isEmpty();
    }

    private static boolean compareWith(List<String> testNames, List<Progress> progresses, ReportSortType type) {
        Report report = new Report(progresses.stream());
        report.sortSectionsBy(type);
        List<ReportSection> sections =  report.getSections();
        List<String> sectionsNames = sections.stream().map(ReportSection::getSectionTitle).collect(Collectors.toList());

        Collections.sort(testNames);
//        testNames.forEach(System.out::println);
        return sectionsNames.equals(testNames);
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
        Progress progress1 = new Progress();
        progress1.setTask(task1);
        progresses.add(progress1);
        Progress progress2 = new Progress();
        progress2.setTask(task2);
        progresses.add(progress2);

        assertThat(compareWith(testNames, progresses, ReportSortType.REPORT_SORT_TYPE_TEAM)).isTrue();
    }

    @Test
    void getSectionsSortByUser() {
        List<String> testNames = new ArrayList<>(Arrays.asList( "c", "b", "a"));
        List<Progress> progresses = new ArrayList<>();
        for (int i = 0; i < testNames.size(); ++i) {
            progresses.add(new Progress(testNames.get(i), (long) i));
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
            Progress progress = new Progress();
            progress.setTask(task);
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
            Progress progress = new Progress();
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