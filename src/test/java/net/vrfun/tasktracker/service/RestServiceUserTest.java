/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.lang.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class RestServiceUserTest {

    @Mock
    private UserAuthenticator userAuthenticator;

    @Mock
    private Users users;

    private RestServiceUser restServiceUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        restServiceUser = new RestServiceUser(userAuthenticator, users);
    }

    @Test
    public void createSuccess() {
        User user = createUser(42L, null);

        when(users.createUser(any())).thenReturn(user);

        ResponseEntity<Long> response = restServiceUser.create(any());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(42L);
    }

    @NonNull
    private User createUser(@Nullable final Long id, @Nullable final String login) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);

        return user;
    }

    @Test
    public void createFail() {
        doThrow(new RuntimeException("Test: Exception on user creation!")).when(users).createUser(any());

        ResponseEntity<Long> response = restServiceUser.create(any());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void editInvalidLogin() {
        ReqUserEdit reqUser = new ReqUserEdit();

        ResponseEntity<Long> response = restServiceUser.edit(reqUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void editAsOwner() {
        ReqUserEdit reqUser = createReqUserEdit("mylogin");
        mockUserAuthenticator("mylogin", null, false);

        when(users.editUser(any())).thenReturn(createUser(42L, null));

        ResponseEntity<Long> response = restServiceUser.edit(reqUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(42L);
    }

    @NonNull
    private ReqUserEdit createReqUserEdit(@Nullable final String login) {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setLogin(login);
        return reqUser;
    }

    private void mockUserAuthenticator(@NonNull final String login, @Nullable Set<Role>roles, boolean isAdmin) {
        when(userAuthenticator.getUserLogin()).thenReturn(login);
        when(userAuthenticator.getUserRoles()).thenReturn(roles);
        when(userAuthenticator.isRoleAdmin()).thenReturn(isAdmin);
    }

    @Test
    public void editAsAdmin() {
        ReqUserEdit reqUser = createReqUserEdit("mylogin");
        mockUserAuthenticator("admin", null, true);

        when(users.editUser(any())).thenReturn(createUser(42L, null));

        ResponseEntity<Long> response = restServiceUser.edit(reqUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(42L);
    }

    @Test
    public void editNoPrivilege() {
        ReqUserEdit reqUser = createReqUserEdit("mylogin");
        mockUserAuthenticator("otherlogin", null, false);

        when(users.editUser(any())).thenReturn(createUser(0L, null));

        ResponseEntity<Long> response = restServiceUser.edit(reqUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void editWithException() {
        ReqUserEdit reqUser = createReqUserEdit("mylogin");
        mockUserAuthenticator("mylogin", null, false);

        doThrow(new RuntimeException("Test: Edit throws exception!")).when(users).editUser(any());

        ResponseEntity<Long> response = restServiceUser.edit(reqUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void deleteSuccess() {
        ResponseEntity<Void> response = restServiceUser.delete(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteFail() {
        doThrow(new RuntimeException("Test: Cannot delete user!")).when(users).deleteUser(anyLong());

        ResponseEntity<Void> response = restServiceUser.delete(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void getUsersAsAdmin() {
        mockUserAuthenticator("admin", null, true);

        List<UserShortInfo> allUsers = createUserShortInfoList();

        when(users.getUsers()).thenReturn(allUsers);

        ResponseEntity<List<UserShortInfo>> response = restServiceUser.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(allUsers.size());
    }

    @NonNull
    private List<UserShortInfo> createUserShortInfoList() {
        return Arrays.asList(
                createUserShortInfo(1L, "admin"),
                createUserShortInfo(11L, "user1"),
                createUserShortInfo(22L, "user2")
        );
    }

    @NonNull
    private UserShortInfo createUserShortInfo(@Nullable final Long id, @Nullable final String login) {
        UserShortInfo user = new UserShortInfo();
        user.setId(id);
        user.setLogin(login);

        return user;
    }

    @Test
    public void getUsersAsNonAdmin() {
        mockUserAuthenticator("someuser", null, false);

        try {
            when(users.getUserByLogin(anyString())).thenReturn(createUserShortInfo(1L, "user"));
        }
        catch (IllegalAccessException exception) {
            fail("Unexpected exception, reason:", exception.getMessage());
        }

        ResponseEntity<List<UserShortInfo>> response = restServiceUser.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getUsersWithException() {
        mockUserAuthenticator("someuser", null, false);

        try {
            doThrow(new RuntimeException("Test: Cannot get user short info!")).when(users).getUserByLogin(anyString());
        }
        catch (IllegalAccessException exception) {
            fail("Unexpected exception, reason:", exception.getMessage());
        }

        ResponseEntity<List<UserShortInfo>> response = restServiceUser.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void getUserSuccess() {
        try {
            when(users.getUserById(anyLong())).thenReturn(createUserShortInfo(42L, "user"));
        }
        catch (IllegalAccessException exception) {
            fail("Unexpected exception, reason:", exception.getMessage());
        }

        ResponseEntity<UserShortInfo> response = restServiceUser.getUser(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(42L);
    }

    @Test
    public void getUserFail() {
        try {
            doThrow(new RuntimeException("Test: User not found!")).when(users).getUserById(anyLong());
        }
        catch (IllegalAccessException exception) {
            fail("Unexpected exception, reason:", exception.getMessage());
        }

        ResponseEntity<UserShortInfo> response = restServiceUser.getUser(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void loginUserLocal() {
        mockUserAuthenticatorWithAuthenticated("user", null, false);

        when(userAuthenticator.loginLocalUser(anyString(), anyString())).thenReturn(true);
        when(userAuthenticator.loginLDAPUser(anyString(), anyString())).thenReturn(false);

        ResponseEntity<RespAuthenticationStatus> response = restServiceUser.loginUser(createReqLogin("mylogin", "mypassword"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAuthenticated()).isTrue();
    }

    @NonNull
    private ReqLogin createReqLogin(@Nullable final String login, @Nullable final String password) {
        ReqLogin reqLogin = new ReqLogin();
        reqLogin.setLogin(login);
        reqLogin.setPassword(password);

        return reqLogin;
    }

    private void mockUserAuthenticatorWithAuthenticated(@Nullable final String login, @Nullable Set<Role> roles, boolean isAdmin) {
        mockUserAuthenticator(login, roles, isAdmin);
        when(userAuthenticator.isUserAuthenticated()).thenReturn(true);
    }

    @Test
    public void loginUserLdap() {
        mockUserAuthenticatorWithAuthenticated("user", null, false);

        when(userAuthenticator.loginLocalUser(anyString(), anyString())).thenReturn(false);
        when(userAuthenticator.loginLDAPUser(anyString(), anyString())).thenReturn(true);

        ResponseEntity<RespAuthenticationStatus> response = restServiceUser.loginUser(createReqLogin("mylogin", "mypassword"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAuthenticated()).isTrue();
    }

    @Test
    public void loginUserFail() {
        mockUserAuthenticator("user", null, false);

        when(userAuthenticator.loginLocalUser(anyString(), anyString())).thenReturn(false);
        when(userAuthenticator.loginLDAPUser(anyString(), anyString())).thenReturn(false);

        ResponseEntity<RespAuthenticationStatus> response = restServiceUser.loginUser(createReqLogin("mylogin", "mypassword"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAuthenticated()).isFalse();
    }

    @Test
    public void logoutUser() {
        ResponseEntity<RespAuthenticationStatus> response = restServiceUser.logoutUser();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void status() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_ADMIN);

        mockUserAuthenticatorWithAuthenticated("mylogin", roles, false);

        ResponseEntity<RespAuthenticationStatus> response = restServiceUser.status();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().getName()).isEqualTo("mylogin");
        assertThat(response.getBody().getRoles()).contains(Role.ROLE_ADMIN);
    }
}
