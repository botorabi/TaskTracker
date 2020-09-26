/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
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
