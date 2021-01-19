/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.common;

/**
 * Common interface for all entities.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public interface EntityContracts {

    /**
     * Get the entity ID.
     */
    Long getId();

    /**
     * Set the entity ID.
     */
    void setId(Long id);
}
