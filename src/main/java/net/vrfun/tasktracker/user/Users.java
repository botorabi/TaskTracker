/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

/**
 * A collection of user related services.
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Service
public class Users {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private UserRoleRepository userRoleRepository;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public Users(
            @NonNull final UserRoleRepository userRoleRepository,
            @NonNull final UserRepository userRepository,
            @NonNull final PasswordEncoder passwordEncoder) {

        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            user.setRealName(reqUser.getRealName());
        }
        if (!StringUtils.isEmpty(reqUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(reqUser.getPassword()));
        }

        if (create) {
            user.setLogin(reqUser.getLogin());
            user.setCreationDate(Instant.now());
        }

        if (reqUser.getRoles() != null) {
            user.setRoles(databaseRolesFromNames(reqUser.getRoles()));
        }

        userRepository.save(user);

        return user;
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
    public List<UserShortInfo> getUsers() {
        List<UserShortInfo> users = userRepository.getUsers();
        for (UserShortInfo user :users) {
            fetchUserRoles(user);
        }
        return users;
    }

    protected void fetchUserRoles(@NonNull UserShortInfo user) {
        Optional<User> foundUser = userRepository.findById(user.getId());
        if (foundUser.isPresent()) {
            user.setRoles(UserShortInfo.createRoleStrings(foundUser.get().getRoles()));
        }
    }

    @NonNull
    public UserShortInfo getUserById(long id) throws IllegalAccessException {
        Optional<UserShortInfo> user = userRepository.getUserById(id);
        user.orElseThrow(() -> new IllegalAccessException("User with given ID does not exist!"));

        fetchUserRoles(user.get());

        return user.get();
    }

    @NonNull
    public UserShortInfo getUserByLogin(@NonNull final String login) throws IllegalAccessException {
        Optional<UserShortInfo> user = userRepository.getUserByLogin(login);
        user.orElseThrow(() -> new IllegalAccessException("User with given login does not exist!"));

        fetchUserRoles(user.get());

        return user.get();
    }
}
