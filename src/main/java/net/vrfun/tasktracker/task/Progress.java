/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.common.BaseEntity;
import org.springframework.lang.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.*;
import java.time.temporal.TemporalField;
import java.util.*;

@Entity
public class Progress extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private Long ownerId;

    @Column(length = 256)
    private String ownerName;

    @OneToMany(targetEntity = Tag.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<Tag> tags;

    @OneToOne(targetEntity = Task.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Task task;

    @Column(nullable=false)
    private Instant dateCreation;

    @Column(nullable=false)
    private LocalDate reportWeek;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false, length = (10 * 1024))
    private String text;

    public Progress() {}

    public Progress(@NonNull final String ownerName, @NonNull final Long ownerId) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.dateCreation = Instant.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    @NonNull
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(@NonNull final Long ownerId) {
        this.ownerId = ownerId;
    }

    @NonNull
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(@NonNull final String loginName) {
        this.ownerName = loginName;
    }

    @Nullable
    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(@Nullable final Collection<Tag> tags) {
        this.tags = tags;
    }

    @Nullable
    public Task getTask() {
        return task;
    }

    public void setTask(@Nullable final Task task) {
        this.task = task;
    }

    @NonNull
    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(@NonNull final Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    @NonNull
    public LocalDate getReportWeek() {
        return reportWeek;
    }

    public void setReportWeek(@NonNull final LocalDate reportWeek) {
        this.reportWeek = reportWeek;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull final String title) {
        this.title = title;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull final String text) {
        this.text = text;
    }
}
