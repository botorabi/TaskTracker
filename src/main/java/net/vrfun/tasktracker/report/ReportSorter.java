package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportSorter {

    @NonNull
    static List<ReportSection> sortByTeam(@NonNull Stream<Progress> progressStream) {
        return sortByTitle(progressStream, ReportSorter::getTeamTitles);
    }

    @NonNull
    static List<ReportSection> sortByUser(@NonNull Stream<Progress> progressStream) {
        return sortByTitle(progressStream, ReportSorter::getUserTitle);
    }

    @NonNull
    static List<ReportSection> sortByTask(@NonNull Stream<Progress> progressStream) {
        return sortByTitle(progressStream, ReportSorter::getTaskTitle);
    }

    @NonNull
    static List<ReportSection> sortByWeek(@NonNull Stream<Progress> progressStream) {
        return sortByTitle(progressStream, ReportSorter::getWeekTitle);
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
        returnSet.add(progress.getReportWeek().toString());
        return returnSet;
    };

    static private List<ReportSection> sortByTitle(Stream<Progress> stream, Function<Progress, Set<String>> titleExtractor) {
        List<ReportSection> sections = new ArrayList<>();
        Set<String> titles = new HashSet<>();

        stream.forEach(s -> titles.addAll(titleExtractor.apply(s)));

        List<String> sortedTitles = new ArrayList<>(titles);
        Collections.sort(sortedTitles);

        sortedTitles.forEach(title -> {
            Stream<Progress> subStream = stream.filter(
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

