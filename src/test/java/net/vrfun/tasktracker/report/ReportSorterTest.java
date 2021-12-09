package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)

class ReportSorterTest {


    static boolean compareWith(List<String> testNames, List<Progress> progresses, Function<Collection<Progress>, List<ReportSection>> a) {
        List<ReportSection> sections =  a.apply(progresses);
        List<String> sectionsNames = sections.stream().map(ReportSection::getSectionTitle).collect(Collectors.toList());

        Collections.sort(testNames);
//        testNames.forEach(System.out::println);
        return sectionsNames.equals(testNames);
    }


    @Test
    void sortByTeam() {
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

        assertThat(compareWith(testNames, progresses, ReportSorter::sortByTeam)).isTrue();
    }

    @Test
    void sortByUser() {
        List<String> testNames = new ArrayList<>(Arrays.asList( "c", "b", "a"));
        List<Progress> progresses = new ArrayList<>();
        for (int i = 0; i < testNames.size(); ++i) {
            progresses.add(new Progress(testNames.get(i), (long) i));
        }
        assertThat(compareWith(testNames, progresses, ReportSorter::sortByUser)).isTrue();
    }

    @Test
    void sortByTask() {
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

        assertThat(compareWith(testNames, progresses, ReportSorter::sortByTask)).isTrue();
    }

    @Test
    void sortByWeek() {
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

        assertThat(compareWith(weekNames, progresses, ReportSorter::sortByWeek)).isTrue();
    }
}