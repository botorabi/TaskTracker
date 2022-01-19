/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.report.docgen.*;
import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.TeamRepository;
import net.vrfun.tasktracker.user.User;
import net.vrfun.tasktracker.user.UserRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Report document composer
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
public class ReportComposer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${company.name: ''}")
    private String companyName;

    public final static String REPORT_DATE_FORMAT = "d. MMMM yyyy";

    private final ProgressRepository progressRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportComposer(@NonNull final ProgressRepository progressRepository,
                          @NonNull final TeamRepository teamRepository,
                          @NonNull final TaskRepository taskRepository,
                          @NonNull final UserRepository userRepository) {

        this.progressRepository = progressRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    ReportSortType sortTypeUiToBackend(@NonNull final String sortTypeFromUi) {
        if (StringUtils.containsIgnoreCase(sortTypeFromUi, "team")) {
            return ReportSortType.REPORT_SORT_TYPE_TEAM;
        } else if (StringUtils.containsIgnoreCase(sortTypeFromUi, "user")) {
            return ReportSortType.REPORT_SORT_TYPE_USER;
        } else if (StringUtils.containsIgnoreCase(sortTypeFromUi, "week")) {
            return ReportSortType.REPORT_SORT_TYPE_WEEK;
        } else if (StringUtils.containsIgnoreCase(sortTypeFromUi, "task")) {
            return ReportSortType.REPORT_SORT_TYPE_TASK;
        }
        return ReportSortType.REPORT_SORT_TYPE_NONE;
    }

    @NonNull
    public ByteArrayOutputStream createTeamReport(@NonNull final List<Long> teamIDs,
                                                  @NonNull final LocalDate fromDate,
                                                  @NonNull final LocalDate toDate,
                                                  @NonNull final ReportFormat reportFormat,
                                                  @NonNull final String title,
                                                  @NonNull final String subTitle,
                                                  @NonNull final String language,
                                                  @NonNull final String sortType) {

        List<ReportSection> sections = getTeamReportSections(teamIDs, fromDate, toDate, sortTypeUiToBackend(sortType));
        return finalizeReport(sections, fromDate, toDate, reportFormat, title, subTitle, language);
    }

    @NonNull
    public ByteArrayOutputStream createUserReport(@NonNull final Long      userId,
                                                  @NonNull final LocalDate fromDate,
                                                  @NonNull final LocalDate toDate,
                                                  @NonNull final ReportFormat reportFormat,
                                                  @NonNull final String title,
                                                  @NonNull final String subTitle,
                                                  @NonNull final String language,
                                                  @NonNull final String sortType) throws IllegalAccessException {

        List<ReportSection> sections = getUserReportSections(userId, fromDate, toDate, sortTypeUiToBackend(sortType));
        return finalizeReport(sections, fromDate, toDate, reportFormat, title, subTitle, language);
    }

    @NonNull
    public ByteArrayOutputStream finalizeReport(@NonNull final List<ReportSection> reportSections,
                                                @NonNull final LocalDate fromDate,
                                                @NonNull final LocalDate toDate,
                                                @NonNull final ReportFormat reportFormat,
                                                @NonNull final String title,
                                                @NonNull final String subTitle,
                                                @NonNull final String language) {

        try {
            ReportI18n reportI18n = loadLocalization(language);
            assert reportI18n != null;
            String footer = companyName + ", Generated by " + appName + " v" + appVersion;
            String periodLocal = reportI18n.translate("period");
            String createdLocal = reportI18n.translate("created");
            String compoundSubtitle = subTitle
                    + "\n " + periodLocal + ": " + fromDate.format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT)) + " - "
                    + toDate.format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT))
                    + "\n " + createdLocal + ": " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT + " / HH:mm"));
            return ReportDocumentCreator.getAs(reportFormat, reportSections, title, compoundSubtitle, "", footer);

        } catch (Throwable throwable) {
            LOGGER.error("Could not create report file, reason: {}", throwable.getMessage());
            throw throwable;
        }
    }


    @NonNull
    public List<ReportSection> getTeamReportSections(@NonNull final List<Long>     teamIDs,
                                                     @NonNull final LocalDate      fromDate,
                                                     @NonNull final LocalDate      toDate,
                                                     @NonNull final ReportSortType sortByType) {

        List<Team> teamList = teamRepository.findAllById(teamIDs);
        if (teamList.isEmpty()) {
            throw new IllegalArgumentException("Invalid team IDs!");
        }

        try {
            List<Progress> progressList = new ArrayList<>();
            Set<String> teamNames = new HashSet<>();
            teamList.forEach(team -> {
                LOGGER.debug("Generating progress for team: {}", team.getName());

                taskRepository.findTeamTasks(team).forEach(task -> {
                    LOGGER.debug(" Generating progress for task: {}", task.getTitle());
                    progressList.addAll(progressRepository.findByTaskIdAndReportWeekBetween(task.getId(), fromDate, toDate));
                });
                teamNames.add(team.getName());
            });
            if (sortByType == ReportSortType.REPORT_SORT_TYPE_TEAM) {
                return ReportSectionGenerator.getSections(progressList.stream(), sortByType, teamNames);
            } else {
                return ReportSectionGenerator.getSections(progressList.stream(), sortByType);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Could not create report file, reason: {}", throwable.getMessage());
            throw throwable;
        }
    }

    @NonNull
    public List<ReportSection> getUserReportSections(@NonNull final Long           userId,
                                                     @NonNull final LocalDate      fromDate,
                                                     @NonNull final LocalDate      toDate,
                                                     @NonNull final ReportSortType sortByType) throws IllegalAccessException {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new IllegalAccessException("User with given login does not exist!"));

        try {
            List<Progress> progresses = progressRepository.findProgressByOwnerIdAndReportWeekBetween(userId, fromDate, toDate);
            return ReportSectionGenerator.getSections(progresses.stream(), sortByType);
        } catch (Throwable throwable) {
            LOGGER.error("Could not create report file, reason: {}", throwable.getMessage());
            throw throwable;
        }
    }

    @Nullable
    protected ReportI18n loadLocalization(@NonNull final String language) {
        ReportI18n.Locale locale;
        if (StringUtils.isEmpty(language) || language.equalsIgnoreCase("en")) {
            locale = ReportI18n.Locale.EN;
        }
        else if (language.equalsIgnoreCase("de")) {
            locale = ReportI18n.Locale.DE;
        }
        else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }

        try {
            return ReportI18n.build(locale);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Could not setup the locale, reason: " + exception.getMessage());
        }
    }
}
