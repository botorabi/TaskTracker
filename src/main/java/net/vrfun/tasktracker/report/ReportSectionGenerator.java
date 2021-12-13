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

    static private List<Progress> getMatchingProgresses(List<Progress> progresses,
                                                        Function<Progress, Stream<String>> fieldExtractor,
                                                        String key)
    {
        return progresses.stream().filter(
                        p -> fieldExtractor.apply(p).anyMatch(key::equals))
                .collect(Collectors.toList());
    }

    /**
     *  Sorts the Progress List according to the field extractors in DECLINING priority
     */
    static private List<Progress> extractAndSubSort(List<Progress> progresses,
                                                    List<Function<Progress, Stream<String>>> extractors)
    {
        if (extractors.isEmpty())
        {
            return progresses;
        }
        Function<Progress, Stream<String>> primaryExtractor = extractors.get(0);

        HashSet<String> keys = progresses.stream().flatMap(primaryExtractor).collect(Collectors.toCollection(HashSet::new));
        List<String> sortedKeys = keys.stream().sorted().collect(Collectors.toList());
        List<Progress> sortedProgresses = new ArrayList<>();
        sortedKeys.forEach(key -> {
            List<Progress> subProgresses = getMatchingProgresses(progresses, primaryExtractor, key);
            if (extractors.size() > 1)
            {
                sortedProgresses.addAll(extractAndSubSort(subProgresses, extractors.subList(1, extractors.size())));
            }
            else
            {
                sortedProgresses.addAll(subProgresses);
            }
        });
        return sortedProgresses;
    }


    static private List<ReportSection> sortByField(List<Progress> progresses, ReportSortType sortByType)
    {
        Function<Progress, Stream<String>> primaryFieldExtractor = getProgressFieldExtractor(sortByType);
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = getSecondaryFieldExtractors(sortByType);

        List<ReportSection> sections = new ArrayList<>();
        Set<String> primaryFields = progresses.stream().flatMap(primaryFieldExtractor).collect(Collectors.toSet());
        if (!primaryFields.isEmpty())
        {
            List<String> sortedFields = new ArrayList<>(primaryFields);
            Collections.sort(sortedFields);
            sortedFields.forEach(field -> {
                List<Progress> progressList = progresses.stream().filter(
                        s -> primaryFieldExtractor.apply(s).anyMatch(field::equals)
                ).collect(Collectors.toList());
                progressList = extractAndSubSort(progressList, secondaryFieldExtractors);

                ///Extracts each progress text and prepends the secondary fields
                List<String> progressStrings =  progressList.stream().map( progress -> {
                    StringBuffer buffer = new StringBuffer();
                    secondaryFieldExtractors.forEach(extractor -> {
                        buffer.append(extractor.apply(progress).collect(Collectors.joining(", ")));
                        buffer.append("  ");
                    });
                    buffer.append("\n");
                    buffer.append(progress.getText());
                    buffer.append("\n");
                    return buffer.toString();
                }).collect(Collectors.toList());

                sections.add(new ReportSection(field, progressStrings));
            });
        }
        return sections;
    }
}
