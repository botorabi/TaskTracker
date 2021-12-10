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

public class Report {

    private List<ReportSection> sections;

    private Stream<Progress> progresses;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public Report()
    {
        sections = new ArrayList<>();
        progresses = new ArrayList<Progress>().stream();
    }

    @NonNull
    public List<ReportSection> getSections() {
        return sections;
    }

    public void addProgress(@NonNull Stream<Progress> progress)
    {
        try {
            this.progresses = Stream.concat(this.progresses, progress);
        } catch (IllegalStateException e) {
            LOGGER.debug("Cannot concatenate progress, because previous progress stream has already been used. Resetting report progresses instead! ");
            resetProgress(progress);
        }
    }

    public void resetProgress(@NonNull Stream<Progress> progresses) {
        this.progresses = progresses;
    }

    public void sortSectionsBy(ReportSortType type)
    {
        Function<Collection<Progress>, List<ReportSection>> sorter;
        switch (type)
        {
            case REPORT_SORT_TYPE_TASK: {
                sorter = Report::sortByTask;
                break;
            }

            case REPORT_SORT_TYPE_TEAM: {
                sorter = Report::sortByTeam;
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                sorter = Report::sortByUser;
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                sorter = Report::sortByWeek;
                break;
            }

            default: {
                return;
            }
        }
        Collection<Progress> progressCollection;
        try {
            progressCollection = progresses.collect(Collectors.toList());
        } catch (IllegalStateException e) {
            LOGGER.error("Cannot resort the Report! Try resetting the progress stream first.");
            return;
        }
        sections = sorter.apply(progressCollection);
    }

    @NonNull
    private static List<ReportSection> sortByTeam(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, Report::getTeamTitles);
    }

    @NonNull
    private static List<ReportSection> sortByUser(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, Report::getUserTitle);
    }

    @NonNull
    private static List<ReportSection> sortByTask(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, Report::getTaskTitle);
    }

    @NonNull
    private static List<ReportSection> sortByWeek(@NonNull Collection<Progress> progresses) {
        return sortByTitle(progresses, Report::getWeekTitle);
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
