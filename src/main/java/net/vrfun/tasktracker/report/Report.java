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

    public Report(@NonNull Stream<Progress> progresses)
    {
        this.sections = new ArrayList<>();
        this.progresses = progresses;
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

    public void sortSectionsBy(ReportSortType type) throws IllegalStateException
    {
        Function<Progress, Stream<String>> titleExtractor;
        switch (type)
        {
            case REPORT_SORT_TYPE_TASK: {
                titleExtractor = Report::getTaskTitle;
                break;
            }

            case REPORT_SORT_TYPE_TEAM: {
                titleExtractor = Report::getTeamTitles;
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                titleExtractor = Report::getUserTitle;
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                titleExtractor = Report::getWeekTitle;
                break;
            }

            default: {
                return;
            }
        }
        sections = sortByTitle(progresses.collect(Collectors.toList()), titleExtractor);
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
