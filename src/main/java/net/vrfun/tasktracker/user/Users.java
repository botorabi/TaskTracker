/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.task.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of user related services.
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Service
public class Users {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final UserRoleRepository userRoleRepository;

    private final UserRepository userRepository;

    private final TeamRepository teamRepository;

    private final TaskRepository taskRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserAuthenticator userAuthenticator;

    @Autowired
    public Users(
            @NonNull final UserRoleRepository userRoleRepository,
            @NonNull final UserRepository userRepository,
            @NonNull final TeamRepository teamRepository,
            @NonNull final TaskRepository taskRepository,
            @NonNull final PasswordEncoder passwordEncoder,
            @NonNull final UserAuthenticator userAuthenticator) {

        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthenticator = userAuthenticator;
    }

    public void setupApplicationRoles() {
        List<UserRole> rolesToAdd = new ArrayList<>();
        List<UserRole> expectedUserRoles = Role.getAllRoles();

        expectedUserRoles.stream().forEach(userRole -> {
            if (!userRoleRepository.findUserRoleByRole(userRole.getRole()).isPresent()) {
                rolesToAdd.add(userRole);
            }
        });

        for(UserRole roleToAdd: rolesToAdd) {
            LOGGER.info("Creating user role {}", roleToAdd.getName());
            userRoleRepository.save(roleToAdd);
        }
    }

    public void setupApplicationUsers() {
        setupApplicationRoles();

        if (userRepository.count() == 0) {
            createAdminUser();
            LOGGER.info("An admin user was created: admin/admin. Don't forget to adapt or remove this user once the system was setup.");
        }
    }

    protected void createAdminUser() throws IllegalArgumentException {
        ReqUserEdit reqUser = new ReqUserEdit();
        reqUser.setRealName("Administrator");
        reqUser.setLogin("admin");
        reqUser.setPassword("admin");
        reqUser.setEmail("no-valid-email");
        Set<String> roleNames = new HashSet<>();
        roleNames.add("ROLE_ADMIN");
        reqUser.setRoles(roleNames);

        createUser(reqUser);
    }

    @NonNull
    public User createUser(@NonNull final ReqUserEdit reqUser) throws IllegalArgumentException {
        Optional<User> foundUser = userRepository.findUserByLogin(reqUser.getLogin());

        if (foundUser.isPresent()) {
            throw new IllegalArgumentException("A user with given login name already exists!");
        }
        if (StringUtils.isEmpty(reqUser.getPassword())) {
            throw new IllegalArgumentException("Password must not be empty!");
        }
        if (StringUtils.isEmpty(reqUser.getLogin())) {
            throw new IllegalArgumentException("Login must not be empty!");
        }
        if (StringUtils.isEmpty(reqUser.getEmail())) {
            throw new IllegalArgumentException("E-Mail must not be empty!");
        }

        return createOrUpdateUser(reqUser, new User(), true);
    }

    @NonNull
    public User editUser(@NonNull final ReqUserEdit reqUser) throws IllegalArgumentException {
        Optional<User> foundUser = userRepository.findById(reqUser.getId());

        if (!foundUser.isPresent()) {
            throw new IllegalArgumentException("A user with given ID does not exist!");
        }
        return createOrUpdateUser(reqUser, foundUser.get(), false);
    }

    public void deleteUser(Long id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Invalid ID");
        }

        Optional<User> foundUser = userRepository.findById(id);

        if (!foundUser.isPresent()) {
            throw new IllegalArgumentException("A user with given ID does not exist!");
        }

        userRepository.delete(foundUser.get());
    }

    @NonNull
    protected User createOrUpdateUser(@NonNull final ReqUserEdit reqUser, @NonNull User user, boolean create) throws IllegalArgumentException {
        if (!StringUtils.isEmpty(reqUser.getRealName())) {
            user.setRealName(reqUser.getRealName().trim());
        }
        if (!StringUtils.isEmpty(reqUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(reqUser.getPassword()));
        }
        if (!StringUtils.isEmpty(reqUser.getEmail())) {
            user.setEmail(reqUser.getEmail().trim());
        }

        if (create) {
            user.setLogin(reqUser.getLogin());
            user.setDateCreation(Instant.now());
        }

        if (reqUser.getRoles() != null) {
            user.setRoles(databaseRolesFromNames(reqUser.getRoles()));
        }

        userRepository.save(user);

        return user;
    }

    @NonNull
    public UserDTO getOrCreateLocalUserFromLdap(@NonNull final ReqLogin reqLogin) {
        Optional<UserDTO> user = userRepository.getUserByLdapLogin(reqLogin.getLogin());
        if (user.isPresent()) {
            fetchUserRoles(user.get());
            return user.get();
        }
        else {
            User newUser = new User();
            ReqUserEdit reqUserEdit = new ReqUserEdit();
            reqUserEdit.setRealName(reqLogin.getLogin());
            reqUserEdit.setLogin(reqLogin.getLogin());
            reqUserEdit.setPassword(reqLogin.getPassword());

            createOrUpdateUser(reqUserEdit, newUser, true);

            newUser.setLdapLogin(reqLogin.getLogin());
            userRepository.save(newUser);

            return new UserDTO(newUser);
        }
    }

    @NonNull
    public List<UserRole> databaseRolesFromNames(@NonNull final Set<String> roleNames) {
        final List<UserRole> roles = new ArrayList<>();
        for(final String roleName: roleNames) {
            if (Role.ROLE_ADMIN.name().equals(roleName)) {
                addRole(roles, Role.ROLE_ADMIN);
            }
            else if (Role.ROLE_AUTHOR.name().equals(roleName)) {
                addRole(roles, Role.ROLE_AUTHOR);
            }
            else if (Role.ROLE_TEAM_LEAD.name().equals(roleName)) {
                addRole(roles, Role.ROLE_TEAM_LEAD);
            }
        }
        return roles;
    }

    protected void addRole(@NonNull final List<UserRole> roles, @NonNull final Role role) {
        Optional<UserRole> foundRole = userRoleRepository.findUserRoleByRole(role);
        foundRole.ifPresent((r) -> roles.add(r));
        foundRole.orElseThrow(() -> new IllegalArgumentException("Missing role '" + role.name() + "'! Make sure it exists in database."));
    }

    @NonNull
    public List<UserDTO> getUsers() {
        List<UserDTO> users = userRepository.getUsers();
        for (UserDTO user: users) {
            fetchUserRoles(user);
        }
        return users;
    }

    protected void fetchUserRoles(@NonNull UserDTO user) {
        Optional<User> foundUser = userRepository.findById(user.getId());
        if (foundUser.isPresent()) {
            user.setRoles(Role.getRolesAsString(foundUser.get().getRoles()));
        }
    }

    @NonNull
    public UserDTO getUserById(long id) throws IllegalAccessException {
        Optional<UserDTO> user = userRepository.getUserById(id);
        user.orElseThrow(() -> new IllegalAccessException("User with given ID does not exist!"));

        fetchUserRoles(user.get());

        return user.get();
    }

    @NonNull
    public UserDTO getUserByLogin(@NonNull final String login) throws IllegalAccessException {
        Optional<UserDTO> user = userRepository.getUserByLogin(login);
        user.orElseThrow(() -> new IllegalAccessException("User with given login does not exist!"));

        fetchUserRoles(user.get());

        return user.get();
    }

    @NonNull
    public List<TaskDTO> getUserTasks(@NonNull final Long userId) throws IllegalAccessException {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new IllegalAccessException("User with given login does not exist!"));

        List<Task> userTasks = taskRepository.findUserTasks(user.get());
        List<Team> userTeams = teamRepository.findUserTeams(user.get());
        userTeams.forEach((team) -> taskRepository.findTeamTasks(team).forEach(userTasks::add));

        List<Task> uniqueUserTasks = removeDuplicateTasks(userTasks);
        uniqueUserTasks.sort(Comparator.comparing(Task::getTitle));

        return uniqueUserTasks.stream()
                .map((task) -> new TaskDTO(task))
                .collect(Collectors.toList());
    }

    private List<Task> removeDuplicateTasks(List<Task> userTasks) {
        HashMap<Long, Task> uniqueTasks = new HashMap<>();
        userTasks.forEach((task) -> uniqueTasks.put(task.getId(), task));
        return new ArrayList<>(uniqueTasks.values());
    }

    @NonNull
    public List<TeamDTO> getUserTeams() throws IllegalAccessException {
        if (userAuthenticator.isRoleAdmin()) {
            return teamRepository.findAll().stream()
                    .map((team) -> new TeamDTO(team))
                    .collect(Collectors.toList());
        }

        Optional<User> user = userRepository.findById(userAuthenticator.getUserId());
        user.orElseThrow(() -> new IllegalAccessException("User with given login does not exist!"));

        List<Team> userTeams = teamRepository.findUserTeams(user.get());

        return userTeams.stream()
                .map((team) -> new TeamDTO(team))
                .collect(Collectors.toList());
    }

    @NonNull
    public List<UserDTO> searchUsers(@NonNull final String filter) {
        return userRepository.searchUser(filter);
    }
}
