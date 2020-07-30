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

@Entity
public class Task extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Instant dateCreation;

    private Instant dateClosed;

    @Column(length = 256, nullable = false)
    private String title;

    @Column(length = 1024)
    private String description;

    public Task() {}

    public Task(@NonNull final String title) {
        this.title = title;
        this.dateCreation = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(@NonNull Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Nullable
    public Instant getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(@Nullable Instant dateClosed) {
        this.dateClosed = dateClosed;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
