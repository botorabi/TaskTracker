/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.lang.NonNull;

import java.time.*;
import java.time.temporal.IsoFields;
import java.util.*;

/**
 * Progress data transfer object
 *
 * @author          boto
 * Creation Date    August 2020
 */
public class ProgressDTO {

    private Long id;
    private String title;
    private String text;
    private Long ownerId;
    private String ownerName;
    private Instant dateCreation;
    private LocalDate reportWeek;
    private Collection<String> tags;
    private Long task;

    public ProgressDTO() {}

    public ProgressDTO(@NonNull final Progress progress) {
        this.id = progress.getId();
        this.title = progress.getTitle();
        this.text = progress.getText();
        this.dateCreation = progress.getDateCreation();
        this.reportWeek = progress.getReportWeek();
        this.ownerId = progress.getOwnerId();
        this.ownerName = progress.getOwnerName();
        if (progress.getTask() != null) {
            this.task = progress.getTask().getId();
        }
        if (progress.getTags() != null) {
            this.tags = new ArrayList<>();
            progress.getTags().forEach((tag) -> this.tags.add(tag.getName()));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setReportWeek(int calendarYear, int calendarWeek) {
        reportWeek = LocalDate.of(calendarYear, 1, 1);
        reportWeek = reportWeek.plusWeeks(calendarWeek);
    }

    public Integer getReportWeek() {
        return reportWeek == null ? null : reportWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public Integer getReportYear() {
        return reportWeek == null ? null : reportWeek.get(IsoFields.WEEK_BASED_YEAR);
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }
}
