/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Request for editing an existing or creating a new task progress
 *
 * @author          boto
 * Creation Date    July 2020
 */
public class ReqProgressEdit {

    private long id;

    private Collection<String> tags;

    private Long task;

    private String title;

    private String text;

    private Integer reportWeek;

    private Integer reportYear;

    public ReqProgressEdit() {}

    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public Collection<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Long getTask() {
        return task;
    }

    @JsonProperty("task")
    public void setTask(Long task) {
        this.task = task;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    public Integer getReportWeek() {
        return reportWeek;
    }

    @JsonProperty("reportWeek")
    public void setReportWeek(Integer reportWeek) {
        this.reportWeek = reportWeek;
    }

    public Integer getReportYear() {
        return reportYear;
    }

    @JsonProperty("reportYear")
    public void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }
}
