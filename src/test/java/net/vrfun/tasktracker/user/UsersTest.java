/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.task.TaskRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class UsersTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserAuthenticator userAuthenticator;

    private Users users;

    private int userCount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        users = new Users(userRoleRepository, userRepository, teamRepository, taskRepository, passwordEncoder, userAuthenticator);
    }

    @Test
    public void setupApplicationRoles() {
        when(userRoleRepository.save(any())).then((Answer) invocation -> {
            userCount++;
            return invocation.getArgument(0);
        });

        when(userRoleRepository.findUserRoleByRole(any())).thenReturn(Optional.empty());

        users.setupApplicationRoles();

        assertThat(userCount).isEqualTo(Role.getAllRoles().size());
    }

    @Test
    public void setupApplicationUsers() {
        assertThatThrownBy(() -> users.setupApplicationUsers()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUserInvalidLogin() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setPassword("mypassword");
        reqUser.setEmail("myemail@mydomain.com");

        assertThatThrownBy(() -> users.createUser(reqUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUserInvalidPassword() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setLogin("mylogin");
        reqUser.setEmail("myemail@mydomain.com");

        assertThatThrownBy(() -> users.createUser(reqUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUserInvalidEmail() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setPassword("mypassword");
        reqUser.setLogin("mylogin");

        assertThatThrownBy(() -> users.createUser(reqUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUserExistingUser() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setLogin("mylogin");

        when(userRepository.findUserByLogin("mylogin")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> users.createUser(reqUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUser() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setLogin("mylogin");
        reqUser.setPassword("mypassword");
        reqUser.setEmail("myemail@mydomain.com");

        when(userRepository.findUserByLogin("mylogin")).thenReturn(Optional.empty());

        Users mockedUsers = mockCreateOrUpdateUser();

        assertThat(mockedUsers.createUser(reqUser)).isNotNull();
    }

    private Users mockCreateOrUpdateUser() {
        Users mockedUsers = spy(users);
        doReturn(new User()).when(mockedUsers).createOrUpdateUser(any(), any(), anyBoolean());
        return mockedUsers;
    }

    @Test
    public void editUserNonExisting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Users mockedUsers = mockCreateOrUpdateUser();

        assertThatThrownBy(() -> mockedUsers.editUser(new ReqUserEdit())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void editUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        Users mockedUsers = mockCreateOrUpdateUser();

        assertThat(mockedUsers.editUser(new ReqUserEdit())).isNotNull();
    }

    @Test
    public void deleteUserNullId() {
        assertThatThrownBy(() -> users.deleteUser(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteUserNonExisting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> users.deleteUser(42L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createOrUpdateUserUpdate() {
        ReqUserEdit reqUser = prepareUserCreateOrEdit();
        User user = new User();

        when(userRoleRepository.findUserRoleByRole(any())).thenReturn(Optional.of(new UserRole()));

        user = users.createOrUpdateUser(reqUser, user, false);

        assertThat(user.getRealName()).isEqualTo(reqUser.getRealName());
        assertThat(user.getPassword()).isNotBlank();
        assertThat(user.getRoles()).hasSize(reqUser.getRoles().size());

        assertThat(user.getLogin()).isNull();
        assertThat(user.getDateCreation()).isNull();
    }

    @Test
    public void createOrUpdateUserCreate() {
        ReqUserEdit reqUser = prepareUserCreateOrEdit();
        User user = new User();

        user = users.createOrUpdateUser(reqUser, user, true);

        assertThat(user.getRealName()).isEqualTo(reqUser.getRealName());
        assertThat(user.getPassword()).isNotBlank();
        assertThat(user.getRoles()).hasSize(reqUser.getRoles().size());

        assertThat(user.getLogin()).isNotNull();
        assertThat(user.getDateCreation()).isNotNull();
    }

    private ReqUserEdit prepareUserCreateOrEdit() {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setRealName("myname");
        reqUser.setLogin("mylogin");
        reqUser.setPassword("mypassword");
        Set<String> roles = new HashSet<>();
        roles.add(Role.ROLE_ADMIN.name());
        reqUser.setRoles(roles);

        when(userRoleRepository.findUserRoleByRole(any())).thenReturn(Optional.of(new UserRole()));

        return reqUser;
    }

    @Test
    public void databaseRolesFromNames() {
        Set<String> roles = new HashSet<>();
        roles.add(Role.ROLE_ADMIN.name());
        roles.add(Role.ROLE_AUTHOR.name());

        Users mockedUsers = spy(users);

        doAnswer(invocation -> {
            List<UserRole> r = invocation.getArgument(0);
            Role role = invocation.getArgument(1);
            r.add(new UserRole(role.name(), "", role));
            return null;
        } ).when(mockedUsers).addRole(any(), any());

        assertThat(mockedUsers.databaseRolesFromNames(roles)).hasSize(roles.size());
    }

    @Test
    public void getUsers() {
        List<UserDTO> dummyUsers = new ArrayList<>();
        dummyUsers.add(new UserDTO());
        dummyUsers.add(new UserDTO());

        doReturn(dummyUsers).when(userRepository).getUsers();

        assertThat(users.getUsers()).hasSize(2);
    }

    @Test
    public void getUserById() {
    }

    @Test
    public void getUserByLogin() {
    }
}
