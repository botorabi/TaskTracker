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

    /**
     * Divide the given Progress Stream into a ReportSection List, ordered by the ReportSortType
     */
    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull ReportSortType sortByType) {
        progresses = progresses.distinct();
        return sortByField(progresses.collect(Collectors.toList()), sortByType);
    }

    static private Stream<String> getNoField(Progress progress) {
        return (new ArrayList<String>().stream());
    }

    static private Stream<String> getTeamFields(Progress progress) {
        Task task = progress.getTask();
        if (task != null) {
            Collection<Team> teams = task.getTeams();
            if (teams != null) {
                return teams.stream().map(Team::getName);
            }
        }
        return (new ArrayList<String>().stream());
    };

    static private Stream<String> getTaskField(Progress progress) {
        String field;
        Task task = progress.getTask();
        List<String> returnList = new ArrayList<>();
        if (task != null) {
            field = task.getTitle();
            returnList.add(field);
        }
        return returnList.stream();
    };

    static private Stream<String> getUserField(Progress progress) {
        List<String> returnList = new ArrayList<>();
        returnList.add(progress.getOwnerName());
        return returnList.stream();
    };

    static private Stream<String> getWeekField(Progress progress) {
        List<String> returnList = new ArrayList<>();
        String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        returnList.add(yearWeek);
        return returnList.stream();
    };

    static private Function<Progress, Stream<String>> getProgressFieldExtractor(@NonNull ReportSortType sortByType) {
        Function<Progress, Stream<String>> fieldExtractor = ReportSectionGenerator::getNoField;
        switch (sortByType)
        {
            case REPORT_SORT_TYPE_TASK: {
                fieldExtractor = ReportSectionGenerator::getTaskField;
                break;
            }

            case REPORT_SORT_TYPE_TEAM: {
                fieldExtractor = ReportSectionGenerator::getTeamFields;
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                fieldExtractor = ReportSectionGenerator::getUserField;
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                fieldExtractor = ReportSectionGenerator::getWeekField;
                break;
            }
        }
        return fieldExtractor;
    }

    private static List<Function<Progress, Stream<String>>> getSecondaryFieldExtractors(ReportSortType primaryField)
    {
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = new ArrayList<>();
        switch(primaryField)
        {
            case REPORT_SORT_TYPE_TEAM: {
                secondaryFieldExtractors.add(ReportSectionGenerator::getUserField);
                secondaryFieldExtractors.add(ReportSectionGenerator::getTaskField);
                secondaryFieldExtractors.add(ReportSectionGenerator::getWeekField);
                break;
            }

            case REPORT_SORT_TYPE_TASK: {
                secondaryFieldExtractors.add(ReportSectionGenerator::getUserField);
                secondaryFieldExtractors.add(ReportSectionGenerator::getWeekField);
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                secondaryFieldExtractors.add(ReportSectionGenerator::getTaskField);
                secondaryFieldExtractors.add(ReportSectionGenerator::getWeekField);
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                secondaryFieldExtractors.add(ReportSectionGenerator::getUserField);
                secondaryFieldExtractors.add(ReportSectionGenerator::getTaskField);
                break;
            }
        }
        return secondaryFieldExtractors;
    }

    static private List<ReportSection> sortByField(List<Progress> progresses, ReportSortType sortByType) {
        Function<Progress, Stream<String>> primaryFieldExtractor = getProgressFieldExtractor(sortByType);
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = getSecondaryFieldExtractors(sortByType);

        List<ReportSection> sections = new ArrayList<>();
        progresses.sort(Comparator.comparing(Progress::getDateCreation));
        Set<String> fields = progresses.stream().flatMap(primaryFieldExtractor).collect(Collectors.toSet());
        if (!fields.isEmpty())
        {
            List<String> sortedFields = new ArrayList<>(fields);
            Collections.sort(sortedFields);
            sortedFields.forEach(field -> {
                List<String> progressList = progresses.stream().filter(
                        s -> primaryFieldExtractor.apply(s).anyMatch(field::equals)
                ).map( progress -> {
                    StringBuffer buffer = new StringBuffer();
                    secondaryFieldExtractors.forEach(extractor -> {
                        buffer.append(extractor.apply(progress).collect(Collectors.joining()));
                    });
                    buffer.append("\n");
                    buffer.append(progress.getText());
                    buffer.append("\n");
                    return buffer.toString();
                }).collect(Collectors.toList());
                sections.add(new ReportSection(field, progressList));
            });
        }
        return sections;
    }
}
