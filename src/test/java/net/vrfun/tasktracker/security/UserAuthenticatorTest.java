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
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserAuthenticatorTest {

    @Mock
    private UserRolesMapper userRolesMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LdapConfiguration ldapConfiguration;

    @Mock
    private BindAuthenticator bindAuthenticator;


    private UserAuthenticator userAuthenticator;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // partially mock the user authenticator
        userAuthenticator = spy(new UserAuthenticator(userRolesMapper, userRepository, passwordEncoder, ldapConfiguration));
    }

    @Test
    public void loginLDAPUserSuccess() {
        when(ldapConfiguration.authenticate(any())).thenReturn(new DirContextAdapter());

        assertThat(userAuthenticator.loginLDAPUser("mylogin", "mypassword")).isTrue();
    }

    @Test
    public void loginLDAPUserFail() {
        when(bindAuthenticator.authenticate(any())).thenReturn(null);

        assertThat(userAuthenticator.loginLDAPUser("mylogin", "mypassword")).isFalse();
    }

    @Test
    public void loginLDAPUserFailWithException() {
        when(bindAuthenticator.authenticate(any())).thenThrow(new RuntimeException("LDAP Exception"));

        assertThat(userAuthenticator.loginLDAPUser("mylogin", "mypassword")).isFalse();
    }

    @Test
    public void loginLocalUserNoExistingUser() {
        when(userRepository.findUserByLogin(any())).thenReturn(Optional.empty());

        assertThat(userAuthenticator.loginLocalUser("mylogin", "mypassword")).isFalse();
    }

    @Test
    public void loginLocalUserWrongPassword() {
        when(userRepository.findUserByLogin(any())).thenReturn(Optional.of(new User()));

        assertThat(userAuthenticator.loginLocalUser("mylogin", "mypassword")).isFalse();
    }


    @Test
    public void loginLocalUserSuccess() {
        User user = new User();
        user.setId(42L);
        user.setPassword("password");

        when(userRepository.findUserByLogin(any())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThat(userAuthenticator.loginLocalUser("mylogin", "mypassword")).isTrue();
    }

    @Test
    public void logout() {
        setupSecurityContextHolder();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        userAuthenticator.logoutUser();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private void setupSecurityContextHolder() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("myname", "mypassword"));
    }

    private void setupEmptySecurityContextHolder() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void checkRole() {
        setupSecurityContextHolderWithAuthorities(Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));

        assertThat(userAuthenticator.checkRole(Role.ROLE_ADMIN.name())).isTrue();
    }

    private void setupSecurityContextHolderWithAuthorities(List<SimpleGrantedAuthority> authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "myname",
                        "mypassword",
                        authorities
                ));
    }

    @Test
    public void checkRoleNotFound() {
        setupSecurityContextHolderWithAuthorities(Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_UNKNOWN.name())));

        assertThat(userAuthenticator.checkRole(Role.ROLE_AUTHOR.name())).isFalse();
    }


    @Test
    public void getUserRoles() {
        setupSecurityContextHolderWithAuthorities(
                Arrays.asList(
                        new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()),
                        new SimpleGrantedAuthority(Role.ROLE_AUTHOR.name()),
                        new SimpleGrantedAuthority(Role.ROLE_TEAM_LEAD.name())
                )
        );

        assertThat(userAuthenticator.getUserRoles()).hasSize(3);
    }

    @Test
    public void getUserRolesEmpty() {
        setupEmptySecurityContextHolder();

        assertThat(userAuthenticator.getUserRoles()).isEmpty();

        setupSecurityContextHolderWithAuthorities(
                Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_UNKNOWN.name())));

        assertThat(userAuthenticator.getUserRoles()).isEmpty();
    }

    @Test
    public void isUserAuthenticatedTrue() {
        setupSecurityContextHolder();

        assertThat(userAuthenticator.isUserAuthenticated()).isTrue();
    }

    @Test
    public void isUserAuthenticatedFalse() {
        setupEmptySecurityContextHolder();

        assertThat(userAuthenticator.isUserAuthenticated()).isFalse();

        setupSecurityContextHolderWithAuthorities(
                Arrays.asList(new SimpleGrantedAuthority(UserRolesMapper.ROLE_ANONYMOUS)));

        assertThat(userAuthenticator.isUserAuthenticated()).isFalse();
    }

    @Test
    public void getUserLogin() {
        setupSecurityContextHolder();

        assertThat(userAuthenticator.getUserLogin()).isNotEmpty();
    }

    @Test
    public void getUserLoginEmpty() {
        setupEmptySecurityContextHolder();

        assertThat(userAuthenticator.getUserLogin()).isEmpty();
    }

    @Test
    public void roleCheckers() {
        setupEmptySecurityContextHolder();

        assertThat(userAuthenticator.isRoleAdmin()).isFalse();
        assertThat(userAuthenticator.isRoleAuthor()).isFalse();
        assertThat(userAuthenticator.isRoleTeamLead()).isFalse();
        assertThat(userAuthenticator.isRoleUser()).isFalse();
    }
}
