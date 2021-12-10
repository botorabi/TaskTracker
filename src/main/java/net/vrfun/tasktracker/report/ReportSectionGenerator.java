package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Task;
import net.vrfun.tasktracker.user.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportSectionGenerator {

    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull ReportSortType sortByType) {
        Function<Progress, Stream<String>> titleExtractor;
        switch (sortByType)
        {
            case REPORT_SORT_TYPE_TASK: {
                titleExtractor = ReportSectionGenerator::getTaskTitle;
                break;
            }

            case REPORT_SORT_TYPE_TEAM: {
                titleExtractor = ReportSectionGenerator::getTeamTitles;
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                titleExtractor = ReportSectionGenerator::getUserTitle;
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                titleExtractor = ReportSectionGenerator::getWeekTitle;
                break;
            }

            default: {
                return new ArrayList<>();
            }
        }
        return sortByTitle(progresses.collect(Collectors.toList()), titleExtractor);
    }

    static private Stream<String> getTeamTitles(Progress progress) {
        Task task = progress.getTask();
        if (task != null) {
            Collection<Team> teams = task.getTeams();
            if (teams != null) {
                return teams.stream().map(Team::getName);
            }
        }
        return (new ArrayList<String>().stream());
    };

    static private Stream<String> getTaskTitle(Progress progress) {
        String title;
        Task task = progress.getTask();
        List<String> returnList = new ArrayList<>();
        if (task != null) {
            title = task.getTitle();
            returnList.add(title);
        }
        return returnList.stream();
    };

    static private Stream<String> getUserTitle(Progress progress) {
        List<String> returnList = new ArrayList<>();
        returnList.add(progress.getOwnerName());
        return returnList.stream();
    };

    static private Stream<String> getWeekTitle(Progress progress) {
        List<String> returnList = new ArrayList<>();
        String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        returnList.add(yearWeek);
        return returnList.stream();
    };

    static private List<ReportSection> sortByTitle(List<Progress> progresses, Function<Progress, Stream<String>> titleExtractor) {
        List<ReportSection> sections = new ArrayList<>();
        progresses.sort(Comparator.comparing(Progress::getDateCreation));
        Set<String> titles = progresses.stream().flatMap(titleExtractor).collect(Collectors.toSet());
        if (!titles.isEmpty())
        {
            List<String> sortedTitles = new ArrayList<>(titles);
            Collections.sort(sortedTitles);
            sortedTitles.forEach(title -> {
                Stream<Progress> subStream = progresses.stream().filter(
                        s -> titleExtractor.apply(s).anyMatch(title::equals)
                );
                sections.add(new ReportSection(title, subStream));
            });
        }
        return sections;
    }
}
