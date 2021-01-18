/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.common;


import java.util.Objects;

/**
 * Base of all entity types. It provides proper methods for serialization.
 *
 * @author          boto
 * Creation Date    July 2020
 */
public abstract class BaseEntity implements EntityContracts {

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if ((object == null) || (this.getClass() != object.getClass())) {
            return false;
        }

        EntityContracts that = (EntityContracts) object;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public String toString() {
        String classPath = getClass().getPackage().getName() + "." + getClass().getSimpleName();
        return classPath + "[ id=" + getId() + " ]";
    }
}
