/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.springframework.lang.NonNull;
import java.util.*;

public enum Role {

    ROLE_UNKNOWN,

    ROLE_ADMIN,

    ROLE_TEAM_LEAD,

    ROLE_AUTHOR;

    public static Role fromString(@NonNull final String roleName) {
        try {
            return valueOf(roleName);
        }
        catch(Throwable throwable) {}
        return ROLE_UNKNOWN;
    }

    @NonNull
    public static List<UserRole> getAllRoles() {
        return Arrays.asList(
                createAdmin(),
                createAuthor(),
                createTeamLead()
        );
    }

    @NonNull
    public static UserRole createAdmin() {
        return new UserRole("Administrator", "System administrator", ROLE_ADMIN);
    }

    @NonNull
    public static UserRole createAuthor() {
        return new UserRole("Author", "Author for task progress entries", ROLE_AUTHOR);
    }

    @NonNull
    public static UserRole createTeamLead() {
        return new UserRole("Team Lead", "Team Leader", ROLE_TEAM_LEAD);
    }
}
