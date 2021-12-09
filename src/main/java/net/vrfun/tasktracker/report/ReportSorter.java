package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.springframework.lang.NonNull;

import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportSorter {

    @NonNull
    static List<ReportSection> sortByTeam(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, ReportSorter::getTeamTitles);
    }

    @NonNull
    static List<ReportSection> sortByUser(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, ReportSorter::getUserTitle);
    }

    @NonNull
    static List<ReportSection> sortByTask(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, ReportSorter::getTaskTitle);
    }

    @NonNull
    static List<ReportSection> sortByWeek(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, ReportSorter::getWeekTitle);
    }

    static private Set<String> getTeamTitles(Progress progress) {
        Task task = progress.getTask();
        if (task != null) {
            Collection<Team> teams = task.getTeams();
            if (teams != null) {
                return teams.stream().map(Team::getName).collect(Collectors.toSet());
            }
        }
        return new HashSet<>();
    };

    static private Set<String> getTaskTitle(Progress progress) {
        String title = "";
        Task task = progress.getTask();
        if (task != null) {
            title = task.getTitle();
        }
        Set<String> returnSet = new HashSet<>();
        returnSet.add(title);
        return returnSet;
    };

    static private Set<String> getUserTitle(Progress progress) {
        Set<String> returnSet = new HashSet<>();
        returnSet.add(progress.getOwnerName());
        return returnSet;
    };

    static private Set<String> getWeekTitle(Progress progress) {
        Set<String> returnSet = new HashSet<>();
        String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        returnSet.add(yearWeek);
        return returnSet;
    };

    static private List<ReportSection> sortByTitle(Collection<Progress> progresses, Function<Progress, Set<String>> titleExtractor) {
        List<ReportSection> sections = new ArrayList<>();
        Set<String> titles = new HashSet<>();
        progresses.forEach(s -> titles.addAll(titleExtractor.apply(s)));

        List<String> sortedTitles = new ArrayList<>(titles);
        Collections.sort(sortedTitles);

        sortedTitles.forEach(title -> {
            Stream<Progress> subStream = progresses.stream().filter(
                s -> {
                    Set<String> progressTitles = titleExtractor.apply(s);
                    return progressTitles.stream().anyMatch(title::equals);
                }
            );
            sections.add(new ReportSection(title, subStream));
        });
        return sections;
    }
}

