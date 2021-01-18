/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.security;

import net.vrfun.tasktracker.user.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class UserRolesMapperTest {

    private UserRolesMapper userRolesMapper;

    @Mock
    private UserRepository userRepository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        userRolesMapper = new UserRolesMapper(userRepository);
    }

    @Test
    public void mapUserRolesNoRoles() {
        when(userRepository.findUserByLogin(any())).thenReturn(Optional.of(new User()));

        assertThat(userRolesMapper.mapUserRoles("mylogin")).isEmpty();
    }

    @Test
    public void mapUserRolesNoExistingUser() {
        when(userRepository.findUserByLogin(any())).thenReturn(Optional.empty());

        assertThat(userRolesMapper.mapUserRoles("mylogin")).isEmpty();
    }

    @Test
    public void mapUserRolesUserWithRoles() {
        User user = new User();
        user.setRoles(Role.getAllRoles());

        when(userRepository.findUserByLogin(any())).thenReturn(Optional.of(user));

        Collection<SimpleGrantedAuthority> authorities = userRolesMapper.mapUserRoles("mylogin");

        assertThat(authorities).hasSize(Role.getAllRoles().size());
    }
}
