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

    private final Tags tags;

    private final UserAuthenticator userAuthenticator;

    @Autowired
    public Progresses(@NonNull final ProgressRepository progressRepository,
                      @NonNull final TaskRepository taskRepository,
                      @NonNull final Tags tags,
                      @NonNull final UserAuthenticator userAuthenticator) {

        this.progressRepository = progressRepository;
        this.taskRepository = taskRepository;
        this.tags = tags;
        this.userAuthenticator = userAuthenticator;
    }

    @NonNull
    public List<Progress> getAll() {
        List<Progress> progs = new ArrayList<>();
        progressRepository.findAll().forEach(progs::add);
        return progs;
    }

    @Nullable
    public Progress get(long id) {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isPresent()) {
            return progress.get();
        }
        return null;
    }

    @NonNull
    public List<Progress> getUserProgress() {
        List<Progress> progs = new ArrayList<>();
        progressRepository.findProgressByOwnerId(userAuthenticator.getUserId()).forEach(progs::add);
        return progs;
    }

    @NonNull
    public List<Progress> getTeamProgress(@NonNull final Long teamId) {
        List<Progress> progs = new ArrayList<>();
        progressRepository.findProgressByTeam(teamId).forEach(progs::add);
        return progs;
    }

    @NonNull
    public Progress create(@NonNull final ReqProgressEdit reqProgressEdit) {
        Progress newProgress = new Progress(userAuthenticator.getUserLogin(), userAuthenticator.getUserId());

        if (reqProgressEdit.getTask() == null) {
            LOGGER.debug("Cannot create progress entry, invalid task ID!");
            throw new IllegalArgumentException("Cannot create progress entry, invalid task ID!");
        }

        if (!StringUtils.isEmpty(reqProgressEdit.getTitle())) {
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
