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

public class ReportSectionGenerator {

    private final List<Progress> progresses;
    private List<ReportSortType> sortByTypes;
    private Set<String> primarySortFieldValues;

    /**
     *
     * @param progresses Contain the report data
     * @return the new ReportSectionGenerator instance
     */
    static ReportSectionGenerator build(@NonNull final List<Progress> progresses) {
        return new ReportSectionGenerator(progresses);
    }

    /**
     * Sorts output
     * @param sortByField Field by which the progresses will be sorted
     */
    public ReportSectionGenerator sortBy(@NonNull final ReportSortType sortByField) {
        this.sortByTypes = List.of(sortByField);
        return this;
    }

    /**
     * Sorts output
     * @param sortByFields Fields by which the progresses will be sorted. The sorting hierarchy is by descending order.
     */
    public ReportSectionGenerator sortBy(@NonNull final List<ReportSortType> sortByFields) {
        this.sortByTypes = sortByFields;
        return this;
    }

    /**
     * Restricts output to specific progresses
     * @param primarySortFieldValue Only progresses which contain this value in their first (or only) sorted by field will be
     *                              taken into account. See {@link #sortBy withSortBy}.
     */
    public ReportSectionGenerator withSortFieldValue(@NonNull final String primarySortFieldValue) {
        this.primarySortFieldValues = Set.of(primarySortFieldValue);
        return this;
    }

    /**
     *  Restricts output to specific progresses
     * @param primarySortFieldValues Only progresses which contain one of these values in their first (or only) sorted by field
     *                               will be taken into account. See  {@link #sortBy withSortBy}.
     */
    public ReportSectionGenerator withSortFieldValues(@NonNull final Set<String> primarySortFieldValues) {
        this.primarySortFieldValues = primarySortFieldValues;
        return this;
    }

    /**
     * @return A list of ReportSection's which contain the data from the progresses
     * @apiNote See {@link #sortBy withSortBy} and {@link #withSortFieldValues withSortFieldValues}
     */
    public List<ReportSection> create() {
        return sortByFields();
    }

    /**
     * Private constructor which sets the default values
     */
    private ReportSectionGenerator(@NonNull final List<Progress> progresses) {
        this.progresses = progresses.stream().distinct().collect(Collectors.toList());
        this.sortByTypes = new ArrayList<>();
        this.primarySortFieldValues = new HashSet<>();
    }

    private static Stream<String> getNoField(@NonNull final Progress progress) {
        return Stream.empty();
    }

    private static Stream<String> getTeamFields(@NonNull final Progress progress) {
        Task task = progress.getTask();
        if (task != null) {
            Collection<Team> teams = task.getTeams();
            if (teams != null) {
                return teams.stream().map(Team::getName);
            }
        }
        return Stream.empty();
    }

    private static Stream<String> getTaskField(@NonNull final Progress progress) {
        String field;
        Task task = progress.getTask();
        List<String> returnList = new ArrayList<>();
        if (task != null) {
            field = task.getTitle();
            returnList.add(field);
        }
        return returnList.stream();
    }

    private static Stream<String> getUserField(@NonNull final Progress progress) {
        List<String> returnList = new ArrayList<>();
        returnList.add(progress.getOwnerName());
        return returnList.stream();
    }

    private static Stream<String> getWeekField(@NonNull final Progress progress) {
        List<String> returnList = new ArrayList<>();
        String yearWeek = progress.getReportWeek().get(ChronoField.YEAR) + " - W" +
                progress.getReportWeek().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        returnList.add(yearWeek);
        return returnList.stream();
    }

    private Function<Progress, Stream<String>> getProgressFieldExtractor(@NonNull final ReportSortType sortByType) {
        Function<Progress, Stream<String>> fieldExtractor = ReportSectionGenerator::getNoField;
        switch (sortByType) {
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
    private List<Function<Progress, Stream<String>>> getSecondaryFieldExtractors(@NotEmpty final List<ReportSortType> extractorFieldTypes) {
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = new ArrayList<>();
        ReportSortType primaryField = extractorFieldTypes.get(0);
        List<ReportSortType> appendingTypes = new ArrayList<>();
        switch (primaryField) {
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
     * Each progress is added if ANY of the extracted field String values matches the key
     */
    private List<Progress> getMatchingProgresses(@NonNull final List<Progress> progresses,
                                                 @NonNull final Function<Progress, Stream<String>> fieldExtractor,
                                                 @NonNull final String key) {
        return progresses
                .stream()
                .filter(p -> fieldExtractor.apply(p).anyMatch(key::equals))
                .collect(Collectors.toList());
    }

    /**
     * Sorts the Progress List according to the field extractors in descending priority
     */
    private List<Progress> extractAndSubSort(@NonNull final List<Progress> progresses,
                                             @NonNull final List<Function<Progress, Stream<String>>> fieldExtractors) {
        if (fieldExtractors.isEmpty()) {
            return progresses;
        }
        var primaryFieldExtractor = fieldExtractors.get(0);

        var fieldValues = progresses.stream().flatMap(primaryFieldExtractor).collect(Collectors.toCollection(HashSet::new));
        List<String> sortedKeys = fieldValues.stream().sorted().collect(Collectors.toList());
        List<Progress> sortedProgresses = new ArrayList<>();
        sortedKeys.forEach(key -> {
            List<Progress> subProgresses = getMatchingProgresses(progresses, primaryFieldExtractor, key);
            if (fieldExtractors.size() > 1) {
                sortedProgresses.addAll(extractAndSubSort(subProgresses, fieldExtractors.subList(1, fieldExtractors.size())));
            } else {
                sortedProgresses.addAll(subProgresses);
            }
        });
        return sortedProgresses;
    }


    private List<ReportSection> sortByFields() {
        List<ReportSection> sections = new ArrayList<>();
        if (sortByTypes.isEmpty()) {
            return sections;
        }
        Function<Progress, Stream<String>> primaryFieldExtractor = getProgressFieldExtractor(sortByTypes.get(0));
        List<Function<Progress, Stream<String>>> secondaryFieldExtractors = getSecondaryFieldExtractors(sortByTypes);

        if (primarySortFieldValues.isEmpty()) {
            primarySortFieldValues = progresses.stream().flatMap(primaryFieldExtractor).collect(Collectors.toSet());
        }

        List<String> sortedFields = new ArrayList<>(primarySortFieldValues);
        Collections.sort(sortedFields);

        sortedFields.forEach(field -> {
            List<Progress> progressList = progresses.stream().filter(
                    s -> primaryFieldExtractor.apply(s).anyMatch(field::equals)
            ).collect(Collectors.toList());
            progressList = extractAndSubSort(progressList, secondaryFieldExtractors);

            ///Extracts each progress text and prepends the secondary fields
            List<ReportSectionBody> progressStrings = progressList.stream().map(progress -> {
                List<String> metaInfoBuffer = new ArrayList<>();

                //Extracted fields might contain more than one value. If so, they are listed with the given delimiter.
                secondaryFieldExtractors.forEach(extractor -> metaInfoBuffer.add(extractor.apply(progress).collect(Collectors.joining(" | "))));

                var sectionBody = new ReportSectionBody();
                sectionBody.setText(progress.getText());
                sectionBody.setSubtitle(progress.getTitle());
                sectionBody.setMetaInformation(String.join(", ", metaInfoBuffer));
                return sectionBody;
            }).collect(Collectors.toList());
            if (!progressStrings.isEmpty()) {
                sections.add(new ReportSection(field, progressStrings));
            }
        });

        return sections;
    }


}
