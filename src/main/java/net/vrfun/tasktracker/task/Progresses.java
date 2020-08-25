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
        List<ProgressShortInfo> progs = new ArrayList<>();
        progressRepository.findAll()
                .forEach((progress -> progs.add(new ProgressShortInfo(progress))));

        return progs;
    }

    @Nullable
    public ProgressShortInfo get(long id) {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isPresent()) {
            return new ProgressShortInfo(progress.get());
        }
        return null;
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

        newProgress.setTitle(reqProgressEdit.getTitle());

        if (!StringUtils.isEmpty(reqProgressEdit.getText())) {
            newProgress.setText(reqProgressEdit.getText());
        }

        setProgressTaskAndTags(newProgress, reqProgressEdit);

        return progressRepository.save(newProgress);
    }

    private void setProgressTaskAndTags(@NonNull final Progress newProgress, @NonNull final ReqProgressEdit reqProgressEdit) {
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

        return progressRepository.save(foundProgress.get());
    }

    private void updateProgressEntryTags(@NonNull final Progress progress, @NonNull final Collection<String> tags) {
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
            if (userAuthenticator.isRoleAdmin() || userAuthenticator.isRoleTeamLead() ||
                    userAuthenticator.getUserId() == progress.get().getOwnerId()) {

                progressRepository.delete(progress.get());
            }
            else {
                throw new IllegalArgumentException("Cannot delete progress entry, permission denied.");
            }
        }
        else {
            throw new IllegalArgumentException("Task progress does not exists.");
        }
    }
}
