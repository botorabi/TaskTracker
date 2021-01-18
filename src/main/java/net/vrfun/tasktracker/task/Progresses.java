/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
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

    public static final int MAX_CALENDAR_WEEKS = 53;

    private final ProgressRepository progressRepository;

    private final TaskRepository taskRepository;

    private final TeamRepository teamRepository;

    private final TagRepository tagRepository;

    private final Tags tags;

    private final UserAuthenticator userAuthenticator;

    @Autowired
    public Progresses(@NonNull final ProgressRepository progressRepository,
                      @NonNull final TaskRepository taskRepository,
                      @NonNull final TeamRepository teamRepository,
                      @NonNull final TagRepository tagRepository,
                      @NonNull final Tags tags,
                      @NonNull final UserAuthenticator userAuthenticator) {

        this.progressRepository = progressRepository;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
        this.tagRepository = tagRepository;
        this.tags = tags;
        this.userAuthenticator = userAuthenticator;
    }

    @NonNull
    public List<ProgressDTO> getAll() {
        if (userAuthenticator.isRoleAdmin() || userAuthenticator.isRoleTeamLead()) {
            List<ProgressDTO> progs = new ArrayList<>();
            int count = (int)progressRepository.count();
            if (count > 0) {
                progressRepository.findAllByOrderByReportWeekDesc(PageRequest.of(0, count))
                        .forEach((progress -> progs.add(new ProgressDTO(progress))));
            }
            return progs;
        }
        else {
            return getUserProgress();
        }
    }

    @NonNull
    public ProgressPagedDTO getPaged(int page, int size) {
        if (userAuthenticator.isRoleAdmin()) {
            List<ProgressDTO> progs = new ArrayList<>();
            progressRepository.findAllByOrderByReportWeekDesc(PageRequest.of(page, size))
                    .forEach((progress -> progs.add(new ProgressDTO(progress))));

            return new ProgressPagedDTO(progressRepository.count(), page, progs);
        }
        else if (userAuthenticator.isRoleTeamLead()) {
            List<Long> userIds = findAllTeamLeadRelatedUsers(userAuthenticator.getUser());
            return getUserProgressPaged(userIds, page, size);
        }
        else {
            return getUserProgressPaged(Arrays.asList(userAuthenticator.getUserId()), page, size);
        }
    }

    @NonNull
    protected List<Long> findAllTeamLeadRelatedUsers(@NonNull final User teamLead) {
        List<Long> userIds = new ArrayList<>();
        teamRepository.findUserTeams(teamLead)
                .forEach((team) -> {
                    if (team.getUsers() != null) {
                        team.getUsers().forEach((user) -> userIds.add(user.getId()));
                    }
                    if (team.getTeamLeaders() != null) {
                        team.getTeamLeaders().forEach((user) -> userIds.add(user.getId()));
                    }
                });

        return userIds;
    }

    @Nullable
    public ProgressDTO get(long id) {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isEmpty()) {
            return null;
        }

        if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead()) {

            if (!progress.get().getOwnerId().equals(userAuthenticator.getUserId())) {
                LOGGER.warn("Attempt to access an unauthorized progress ({}) by user {}",
                        progress.get().getId(), userAuthenticator.getUserLogin());

                return null;
            }
        }

        return new ProgressDTO(progress.get());
    }

    @NonNull
    public List<ProgressDTO> getUserProgress() {
        List<ProgressDTO> progs = new ArrayList<>();
        int count = (int)progressRepository.countProgressByOwnerIdIn(Arrays.asList(userAuthenticator.getUserId()));
        if (count > 0) {
            progressRepository.findProgressByOwnerIdInOrderByReportWeekDesc(
                    Arrays.asList(userAuthenticator.getUserId()),PageRequest.of(0, count))
                    .forEach((progress -> progs.add(new ProgressDTO(progress))));
        }
        return progs;
    }

    @NonNull
    public ProgressPagedDTO getUserProgressPaged(@NonNull final List<Long> ownerIds, int page , int size) {
        List<ProgressDTO> progs = new ArrayList<>();
        long totalCount = progressRepository.countProgressByOwnerIdIn(ownerIds);
        if (totalCount > 0) {
            progressRepository.findProgressByOwnerIdInOrderByReportWeekDesc(ownerIds, PageRequest.of(page, size))
                    .forEach((progress -> progs.add(new ProgressDTO(progress))));
        }
        return new ProgressPagedDTO(totalCount, page, progs);
    }

    @NonNull
    public List<ProgressDTO> getTeamProgress(@NonNull final Long teamId) {
        List<ProgressDTO> progs = new ArrayList<>();
        progressRepository.findByTaskId(teamId)
                .forEach((progress -> progs.add(new ProgressDTO(progress))));

        return progs;
    }

    @NonNull
    public Progress create(@NonNull final ReqProgressEdit reqProgressEdit) {
        Progress newProgress = new Progress(userAuthenticator.getUserLogin(), userAuthenticator.getUserId());

        if (reqProgressEdit.getTask() == null) {
            LOGGER.debug("Cannot create progress entry, invalid task ID!");
            throw new IllegalArgumentException("Cannot create progress entry, invalid task ID!");
        }

        if (!checkTaskBelongsToUser(userAuthenticator.getUser(), reqProgressEdit.getTask())) {
            LOGGER.debug("Cannot create progress entry, user {} has no access to task {}!",
                    reqProgressEdit.getTask(),
                    userAuthenticator.getUserLogin());

            throw new IllegalArgumentException("Cannot create progress entry, user has no access to task!");
        }

        if (StringUtils.isEmpty(reqProgressEdit.getTitle())) {
            LOGGER.debug("Cannot create progress entry, invalid progress title!");
            throw new IllegalArgumentException("Cannot create progress entry, invalid progress title!");
        }

        if ((reqProgressEdit.getReportWeek() == null) || (reqProgressEdit.getReportYear() == null)) {
            LOGGER.debug("Cannot create progress entry, invalid calendar week!");
            throw new IllegalArgumentException("Cannot create progress entry, missing calendar week!");
        }

        newProgress.setTitle(reqProgressEdit.getTitle());

        if (!StringUtils.isEmpty(reqProgressEdit.getText())) {
            newProgress.setText(reqProgressEdit.getText());
        }

        setProgressTaskAndTags(newProgress, reqProgressEdit);

        setReportWeek(newProgress, reqProgressEdit.getReportWeek(), reqProgressEdit.getReportYear());

        return progressRepository.save(newProgress);
    }

    protected boolean checkTaskBelongsToUser(@NonNull final User user, final long taskId) {
        List<Task> userTasks = taskRepository.findUserTasks(user);
        for (Task task: userTasks) {
            if (task.getId().equals(taskId)) {
                return true;
            }
        }

        List<Team> userTeams = teamRepository.findUserTeams(user);
        for (Team team: userTeams) {
            for (Task task : taskRepository.findTeamTasks(team)) {
                if (task.getId().equals(taskId)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void setReportWeek(@NonNull final Progress newProgress, int reportWeek, int reportYear) {
        if (reportWeek <= 0 || reportWeek > MAX_CALENDAR_WEEKS) {
            LOGGER.debug("Invalid calendar week {}", reportWeek);
            throw new IllegalArgumentException("Invalid calendar week " + reportWeek);
        }

        if (!userAuthenticator.isRoleAdmin() && !userAuthenticator.isRoleTeamLead() &&
                !checkWeekDistance(getLocalDate(), reportWeek, reportYear)) {

            LOGGER.debug("Invalid calendar week or year! Max allowed distance is " + MAX_CALENDAR_WEEK_DISTANCE + " weeks.");
            throw new IllegalArgumentException("Invalid calendar week or year! Max allowed distance is " + MAX_CALENDAR_WEEK_DISTANCE + " weeks.");
        }

        LocalDate date = LocalDate.of(reportYear, 1, getFirstThursdayInYear(reportYear));
        date = date.plusWeeks(reportWeek - 1);

        newProgress.setReportWeek(date);
    }

    protected int getFirstThursdayInYear(int reportYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, reportYear);
        int dateFirstMonday = calendar.get(Calendar.DATE);
        return dateFirstMonday;
    }

    @NonNull
    protected LocalDate getLocalDate() {
        return LocalDate.now();
    }

    protected boolean checkWeekDistance(@NonNull final LocalDate localDate, int reportWeek, int reportYear) {
        int currentWeek = localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = localDate.get(IsoFields.WEEK_BASED_YEAR);

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
        if (task.isEmpty()) {
            LOGGER.debug("Cannot set progress task. Task with given ID does not exist!");
            throw new IllegalArgumentException("Cannot set progress task. Task with given ID does not exist!");
        }
        newProgress.setTask(task.get());

        Collection<Tag> progressTags = new ArrayList<>();
        if (reqProgressEdit.getTags() != null) {
            reqProgressEdit.getTags().forEach((tagName) -> progressTags.add(tags.getOrCreate(tagName)));
        }
        newProgress.setTags(progressTags);
    }

    @NonNull
    public Progress editProgress(@NonNull final ReqProgressEdit reqProgressEdit) {
        Optional<Progress> foundProgress = progressRepository.findById(reqProgressEdit.getId());
        if (foundProgress.isEmpty()) {
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

        if ((reqProgressEdit.getTask() != null) && (foundProgress.get().getTask() != null) &&
                (!foundProgress.get().getTask().getId().equals(reqProgressEdit.getTask()))) {

            Optional<Task> task = taskRepository.findById(reqProgressEdit.getTask());
            task.orElseThrow(() -> new IllegalArgumentException("Task with given ID does not exist!"));
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

    public void delete(long id) {
        Optional<Progress> progress = progressRepository.findById(id);
        progress.orElseThrow(() ->new IllegalArgumentException("Task progress does not exists."));

        if (userAuthenticator.isRoleAdmin() || userAuthenticator.isRoleTeamLead()) {
            progressRepository.delete(progress.get());
            return;
        }

        if (!progress.get().getOwnerId().equals(userAuthenticator.getUserId())) {
            LOGGER.warn("Attempt to delete a progress ({}) by unauthorized user {}",
                    progress.get().getId(), userAuthenticator.getUserLogin());

            throw new IllegalArgumentException("Cannot delete progress entry, unauthorized access.");
        }

        int reportYear = progress.get().getReportWeek().get(IsoFields.WEEK_BASED_YEAR);
        int reportWeek = progress.get().getReportWeek().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (!checkWeekDistance(getLocalDate(), reportWeek, reportYear)) {
            LOGGER.warn("Attempt to delete a progress ({}) by user {} with invalid max week distance",
                    progress.get().getId(), userAuthenticator.getUserLogin());

            throw new IllegalArgumentException("Cannot delete progress entry, invalid max week distance.");
        }
        else {
            progressRepository.delete(progress.get());
        }
    }
}
