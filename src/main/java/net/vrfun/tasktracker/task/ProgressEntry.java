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
import java.time.Instant;
import java.util.Collection;

@Entity
public class ProgressEntry extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false, length = 256)
    private String ownerName;

    @OneToMany(targetEntity = Tag.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<Tag> tags;

    @OneToOne(targetEntity = Task.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Task task;

    @Column(nullable=false)
    private Instant date;

    @Column(nullable=false, length = 1024)
    private String text;

    public ProgressEntry() {}

    public ProgressEntry(@NonNull final String ownerName) {
        this.ownerName = ownerName;
        this.date = Instant.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(@NonNull String loginName) {
        this.ownerName = loginName;
    }

    @Nullable
    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(@Nullable Collection<Tag> tags) {
        this.tags = tags;
    }

    @Nullable
    public Task getTask() {
        return task;
    }

    public void setTask(@Nullable Task task) {
        this.task = task;
    }

    @NonNull
    public Instant getDate() {
        return date;
    }

    public void setDate(@NonNull Instant date) {
        this.date = date;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }
}
