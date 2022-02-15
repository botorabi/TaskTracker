/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UserDTOTest {

    @Test
    public void createRoleStrings() {
        List<UserRole> userRoles = Role.getAllRoles();
        Collection<String> roleStrings = Role.getRolesAsString(userRoles);

        assertThat(roleStrings).hasSize(userRoles.size());
        assertThat(roleStrings).contains(Role.ROLE_ADMIN.name());
        assertThat(roleStrings).contains(Role.ROLE_AUTHOR.name());
    }
}
