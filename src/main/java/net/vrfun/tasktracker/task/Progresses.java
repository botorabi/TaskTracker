/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.security.UserAuthenticator;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Utilities for handling with task Progress
 *
 * @author          boto
 * Creation Date    August 2020
 */
@Service
public class Progresses {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_CALENDAR_WEEK_DISTANCE = 4;

    private static final int MAX_CALENDAR_WEEKS = 53;

    private final ProgressRepository progressRepository;

    private final TaskRepository taskRepository;

    private final TagRepository tagRepository;

    private final Tags tags;

    private final UserAuthenticator userAuthenticator;

    @Autowired
    public Progresses(@NonNull final ProgressRepository progressRepository,
                      @NonNull final TaskRepository taskRepository,
                      @NonNull final TagRepository tagRepository,
                      @NonNull final Tags tags,
                      @NonNull final UserAuthenticator userAuthenticator) {

        this.progressRepository = progressRepository;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.tags = tags;
        this.userAuthenticator = userAuthenticator;
    }

    @NonNull
    public List<ProgressShortInfo> getAll() {
        if (userAuthenticator.isRoleAdmin() || userAuthenticator.isRoleTeamLead()) {

            List<ProgressShortInfo> progs = new ArrayList<>();
            progressRepository.findAll()
                    .forEach((progress -> progs.add(new ProgressShortInfo(progress))));

            return progs;
        }
        else {
            return getUserProgress();
        }
    }

    @Nullable
    public ProgressShortInfo get(long id) {
        Optional<Progress> progress = progressRepository.findById(id);
        if (!progress.isPresent()) {
            return null;
        }

        if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead()) {

            if (!progress.get().getOwnerId().equals(userAuthenticator.getUserId())) {
                LOGGER.warn("Attempt to access an unauthorized progress ({}) by user {}",
                        progress.get().getId(), userAuthenticator.getUserLogin());

                return null;
            }
        }

        return new ProgressShortInfo(progress.get());
    }

    @NonNull
    public List<ProgressShortInfo> getUserProgress() {
        List<ProgressShortInfo> progs = new ArrayList<>();
        progressRepository.findProgressByOwnerId(userAuthenticator.getUserId())
                .forEach((progress -> progs.add(new ProgressShortInfo(progress))));

        return progs;
    }

    @NonNull
    public List<ProgressShortInfo> getTeamProgress(@NonNull final Long teamId) {
        List<ProgressShortInfo> progs = new ArrayList<>();
        progressRepository.findProgressByTeam(teamId)
                .forEach((progress -> progs.add(new ProgressShortInfo(progress))));

        return progs;
    }

    @NonNull
    public Progress create(@NonNull final ReqProgressEdit reqProgressEdit) {
        Progress newProgress = new Progress(userAuthenticator.getUserLogin(), userAuthenticator.getUserId());

        if (reqProgressEdit.getTask() == null) {
            LOGGER.debug("Cannot create progress entry, invalid task ID!");
            throw new IllegalArgumentException("Cannot create progress entry, invalid task ID!");
        }

        if (StringUtils.isEmpty(reqProgressEdit.getTitle())) {
            LOGGER.debug("Cannot create progress entry, invalid progress title!");
            throw new IllegalArgumentException("Cannot create progress entry, invalid progress title!");
        }

        if ((reqProgressEdit.getReportWeek() == null) || (reqProgressEdit.getReportYear() == null)) {
            LOGGER.debug("Cannot create progress entry, invalid calendar week!");
            throw new IllegalArgumentException("Cannot create progress entry, calendar week!");
        }

        newProgress.setTitle(reqProgressEdit.getTitle());

        if (!StringUtils.isEmpty(reqProgressEdit.getText())) {
            newProgress.setText(reqProgressEdit.getText());
        }

        setProgressTaskAndTags(newProgress, reqProgressEdit);

        setReportWeek(newProgress, reqProgressEdit.getReportWeek(), reqProgressEdit.getReportYear());

        return progressRepository.save(newProgress);
    }

    protected void setReportWeek(@NonNull final Progress newProgress, int reportWeek, int reportYear) {
        if (reportWeek <= 0 || reportWeek > 53) {
            LOGGER.debug("Invalid calendar week {}", reportWeek);
            throw new IllegalArgumentException("Invalid calendar week " + reportWeek);
        }

        if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead() &&
                !checkWeekDistance(reportWeek, reportYear)) {

            LOGGER.debug("Invalid calendar week or year! Max allowed distance is " + MAX_CALENDAR_WEEK_DISTANCE + " weeks.");
            throw new IllegalArgumentException("Invalid calendar week or year! Max allowed distance is " + MAX_CALENDAR_WEEK_DISTANCE + " weeks.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, reportWeek);
        calendar.set(Calendar.YEAR, reportYear);
        newProgress.setReportWeek(calendar);
    }

    protected boolean checkWeekDistance(int reportWeek, int reportYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        if (Math.abs(currentYear - reportYear) > 1) {
            return false;
        }

        if (currentYear == reportYear) {
            return (Math.abs(reportWeek - currentWeek) <= MAX_CALENDAR_WEEK_DISTANCE);
        }
        else if (currentYear < reportYear) {
            return (MAX_CALENDAR_WEEKS - currentWeek + reportWeek) <= MAX_CALENDAR_WEEK_DISTANCE;
        }
        else { // currentYear > reportYear
            return (MAX_CALENDAR_WEEKS + currentWeek - reportWeek) <= MAX_CALENDAR_WEEK_DISTANCE;
        }
    }

    protected void setProgressTaskAndTags(@NonNull final Progress newProgress, @NonNull final ReqProgressEdit reqProgressEdit) {
        Optional<Task> task = taskRepository.findById(reqProgressEdit.getTask());
        if (!task.isPresent()) {
            LOGGER.debug("Cannot set progress task. Task with given ID does not exist!");
            throw new IllegalArgumentException("Cannot set progress task. Task with given ID does not exist!");
        }
        newProgress.setTask(task.get());

        Collection<Tag> progressTags = new ArrayList<>();
        if (reqProgressEdit.getTags() != null) {
            reqProgressEdit.getTags()
                    .stream()
                    .forEach((tagName) -> progressTags.add(tags.getOrCreate(tagName)));
        }
        newProgress.setTags(progressTags);
    }

    @NonNull
    public Progress editProgress(@NonNull final ReqProgressEdit reqProgressEdit) throws IllegalAccessException {
        Optional<Progress> foundProgress = progressRepository.findById(reqProgressEdit.getId());
        if (!foundProgress.isPresent()) {
            throw new IllegalArgumentException("A progress entry with given ID does not exist!");
        }

        if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead()) {
            if (!foundProgress.get().getOwnerId().equals(userAuthenticator.getUserId())) {

                LOGGER.warn("Attempt to access an unauthorized progress ({}) by user {}",
                        foundProgress.get().getId(), userAuthenticator.getUserLogin());

                throw new IllegalArgumentException("Attempt to access an unauthorized progress entry!");
            }
        }

        if (!StringUtils.isEmpty(reqProgressEdit.getTitle())) {
            foundProgress.get().setTitle(reqProgressEdit.getTitle());
        }

        if (!StringUtils.isEmpty(reqProgressEdit.getText())) {
            foundProgress.get().setText(reqProgressEdit.getText());
        }

        if ((reqProgressEdit.getTask() != null) &&
                (foundProgress.get().getTask().getId() != reqProgressEdit.getTask())) {

            Optional<Task> task = taskRepository.findById(reqProgressEdit.getTask());
            task.orElseThrow(() -> new IllegalAccessException("Task with given ID does not exist!"));
            foundProgress.get().setTask(task.get());
        }

        if (reqProgressEdit.getTags() != null) {
            updateProgressEntryTags(foundProgress.get(), reqProgressEdit.getTags());
        }

        if (reqProgressEdit.getReportWeek() != null && reqProgressEdit.getReportYear() != null) {
            setReportWeek(foundProgress.get(), reqProgressEdit.getReportWeek(), reqProgressEdit.getReportYear());
        }

        return progressRepository.save(foundProgress.get());
    }

    protected void updateProgressEntryTags(@NonNull final Progress progress, @NonNull final Collection<String> tags) {
        List<Tag> progressTags = new ArrayList<>();
        tags.forEach((tagName) -> {
            Optional<Tag> foundTag = tagRepository.findTagByName(tagName);
            if (foundTag.isEmpty()) {
                Tag newTag = new Tag(StringUtils.trimAllWhitespace(tagName));
                progressTags.add(tagRepository.save(newTag));
            }
            else {
                progressTags.add(foundTag.get());
            }
        });
        progress.setTags(progressTags);
    }

    public void delete(long id) throws IllegalArgumentException {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isPresent()) {
            int reportYear = progress.get().getReportWeek().get(Calendar.YEAR);
            int reportWeek = progress.get().getReportWeek().get(Calendar.WEEK_OF_YEAR);

            if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead() &&
                !checkWeekDistance(reportWeek, reportYear)) {

                LOGGER.warn("Attempt to delete a progress ({}) by user {} with invalid max week distance",
                        progress.get().getId(), userAuthenticator.getUserLogin());

                throw new IllegalArgumentException("Cannot delete progress entry, invalid max week distance.");
            }
            else {
                progressRepository.delete(progress.get());
            }
        }
        else {
            throw new IllegalArgumentException("Task progress does not exists.");
        }
    }
}
