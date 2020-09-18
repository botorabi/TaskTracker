/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.*;
import net.vrfun.tasktracker.user.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utilities for report generation
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
public class Reports {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ProgressRepository progressRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public Reports(@NonNull final ProgressRepository progressRepository,
                   @NonNull final TeamRepository teamRepository,
                   @NonNull final TaskRepository taskRepository) {

        this.progressRepository = progressRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
    }

    public ByteArrayResource createTeamReportText(List<Long> teamIDs) throws IOException {

        //! TODO check access permissions

        List<Team> teamList = new ArrayList<>();
        teamRepository.findAllById(teamIDs).forEach((team -> teamList.add(team)));
        if (teamList.isEmpty()) {
            throw new IllegalArgumentException("Invalid team IDs!");
        }

        ReportGeneratorPlainText reportGeneratorPlainText = new ReportGeneratorPlainText();
        try (ByteArrayOutputStream byteArrayOutputStream = reportGeneratorPlainText.begin()) {

            reportGeneratorPlainText.generateCoverPage("Team Progress Report", "AVM GmbH, R&D" +
                    "\nCreated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM.dd.yyyy - HH:mm")));

            teamList.forEach((team -> {
                LOGGER.debug("Generating progress for team: {}", team.getName());

                reportGeneratorPlainText.sectionBegin("Team '" + team.getName() + "'");

                taskRepository.findTeamTasks(team).forEach((task -> {
                    LOGGER.debug(" Generating progress for task: {}", task.getTitle());
                    List<Progress> progressList = progressRepository.findByTaskId(task.getId());

                    reportGeneratorPlainText.sectionAppend(progressList);
                }));

                reportGeneratorPlainText.sectionEnd();
            }));

            reportGeneratorPlainText.end();

            return new ByteArrayResource(byteArrayOutputStream.toByteArray());

        } catch (Throwable throwable) {
            LOGGER.error("Could not create report file, reason: {}", throwable.getMessage());
            throw throwable;
        }
    }
}
