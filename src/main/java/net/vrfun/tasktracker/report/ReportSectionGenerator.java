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
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportSectionGenerator {

    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull final ReportSortType sortByType) {

        return getSections(progresses, sortByType, new HashSet<>());
    }

    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull final List<ReportSortType> sortByTypes) {
        return getSections(progresses, sortByTypes, new HashSet<>());
    }

    /**
     *  Divide the given Progress Stream into a ReportSection List
     * @param progresses The stream which shall be ordered
     * @param sortByType The field after which the progresses will be ordered
     * @param targetFields Reduces the output to entries where sortByType equals one of these values
     */
    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull final ReportSortType sortByType, Set<String> targetFields) {
        progresses = progresses.distinct();
        return sortByFields(progresses.collect(Collectors.toList()), List.of(sortByType), targetFields);
    }

    /**
     *  Divide the given Progress Stream into a ReportSection List, ordered by the ReportSortType
     * @param progresses The stream which shall be ordered
     * @param sortByTypes The fields after which the progresses will be ordered. Their order is important!
     *                    The first one determines the section title.
     *                    Others are optional and may describe an additional ordering hierarchy
     * @param targetFields Reduces the output to entries where sortByType equals one of these values
     */
    static List<ReportSection> getSections(@NonNull Stream<Progress> progresses, @NonNull final List<ReportSortType> sortByTypes, Set<String> targetFields) {
        progresses = progresses.distinct();
        return sortByFields(progresses.collect(Collectors.toList()), sortByTypes, targetFields);
    }

    static private Stream<String> getNoField(final Progress progress) {
        return Stream.empty();
    }

    static private Stream<String> getTeamFields(final Progress progress) {
        Task task = progress.getTask();
        if (task != null) {
            Collection<Team> teams = task.getTeams();
            if (teams != null) {
                return teams.stream().map(Team::getName);
            }
        }
        return Stream.empty();
    }

    static private Stream<String> getTaskField(final Progress progress) {
        String field;
        Task task = progress.getTask();
        List<String> returnList = new ArrayList<>();
        if (task != null) {
            field = task.getTitle();
            returnList.add(field);
        }
        return returnList.stream();
    }

    static private Stream<String> getUserField(final Progress progress) {
        List<String> returnList = new ArrayList<>();
        returnList.add(progress.getOwnerName());
        return returnList.stream();
    }

    static private Stream<String> getWeekField(final Progress progress) {
        List<String> returnList = new ArrayList<>();
        String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        returnList.add(yearWeek);
        return returnList.stream();
    }

    static private Function<Progress, Stream<String>> getProgressFieldExtractor(@NonNull final ReportSortType sortByType) {
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

    /**
     * @param extractorFieldTypes List of extractor field types, INCLUDING the primary field! The primary field is used
     *                            to determine the sorting hierarchy of secondary fields if any are not already given
     * @return A List of unique of field extraction functions
     */
    private static List<Function<Progress, Stream<String>>> getSecondaryFieldExtractors(@NotEmpty final List<ReportSortType> extractorFieldTypes)
    {
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = new ArrayList<>();
        ReportSortType primaryField = extractorFieldTypes.get(0);
        List<ReportSortType> appendingTypes = new ArrayList<>();
        switch(primaryField)
        {
            case REPORT_SORT_TYPE_TEAM: {
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_USER);
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_TASK);
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_WEEK);
                break;
            }

            case REPORT_SORT_TYPE_TASK: {
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_USER);
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_WEEK);
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_TASK);
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_WEEK);
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_USER);
                appendingTypes.add(ReportSortType.REPORT_SORT_TYPE_TASK);
                break;
            }
        }
        Set<ReportSortType> uniqueTypes = new LinkedHashSet<>(extractorFieldTypes.subList(1, extractorFieldTypes.size()));
        uniqueTypes.addAll(appendingTypes);
        uniqueTypes.forEach(type -> secondaryFieldExtractors.add(getProgressFieldExtractor(type)));
        return secondaryFieldExtractors;
    }

    /**
     *  Each progress is added if ANY of the extracted field String values matches the key
     */
    static private List<Progress> getMatchingProgresses(final List<Progress> progresses,
                                                        final Function<Progress, Stream<String>> fieldExtractor,
                                                        final String key)
    {
        return progresses
                .stream()
                .filter(p -> fieldExtractor.apply(p).anyMatch(key::equals))
                .collect(Collectors.toList());
    }

    /**
     *  Sorts the Progress List according to the field extractors in DECLINING priority
     */
    static private List<Progress> extractAndSubSort(final List<Progress> progresses,
                                                    final List<Function<Progress, Stream<String>>> extractors)
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


    static private List<ReportSection> sortByFields(final List<Progress> progresses, final List<ReportSortType> sortTypes, Set<String> primaryFields)
    {
        List<ReportSection> sections = new ArrayList<>();
        if (sortTypes.isEmpty())
        {
            return sections;
        }
        Function<Progress, Stream<String>> primaryFieldExtractor = getProgressFieldExtractor(sortTypes.get(0));
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = getSecondaryFieldExtractors(sortTypes);

        if (primaryFields.isEmpty()) {
            primaryFields = progresses.stream().flatMap(primaryFieldExtractor).collect(Collectors.toSet());
        }

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
                    List<String> buffer = new ArrayList<>();
                    secondaryFieldExtractors.forEach(extractor -> buffer.add(extractor.apply(progress).collect(Collectors.joining(" | "))));
                    return String.join(", ", buffer)
                            + "\n\n"
                            + progress.getText()
                            + "\n\n";
                }).collect(Collectors.toList());
                if (!progressStrings.isEmpty()) {
                    sections.add(new ReportSection(field, progressStrings));
                }
            });
        }
        return sections;
    }


}
