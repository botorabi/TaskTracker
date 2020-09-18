/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.security;

import net.vrfun.tasktracker.user.*;
import net.vrfun.tasktracker.user.Role;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.lang.NonNull;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;


/**
 * A service responsible for performing user authentication and role check.
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Service
public class UserAuthenticator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private LdapConfiguration ldapConfiguration;

    private UserRolesMapper userRolesMapper;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserAuthenticator() {
    }

    @Autowired
    public UserAuthenticator(@NonNull final UserRolesMapper userRolesMapper,
                             @NonNull final UserRepository userRepository,
                             @NonNull final PasswordEncoder passwordEncoder,
                             @NonNull final LdapConfiguration ldapConfiguration) {

        this.userRolesMapper = userRolesMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ldapConfiguration = ldapConfiguration;

        this.ldapConfiguration.setup();
    }

    @Bean
    public UserAuthenticator createUserAuthenticator() {
        return new UserAuthenticator();
    }

    /**
     * Try to authenticate a user via LDAP. If successful then a user role mapping is performed.
     */
    public boolean loginLDAPUser(@NonNull final String login, @NonNull final String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        try {
            DirContextOperations user = ldapConfiguration.authenticate(authentication);
            if (user != null) {
                String name = user.attributeExists("name") ? user.getStringAttribute("name") : login;
                mapUserRoles(0L, name, login);
                return true;
            }
            return false;
        } catch (Throwable throwable) {
            LOGGER.warn("Could not login the user {}, reason: {}", login, throwable.getMessage());
        }

        return false;
    }

    /**
     * Try to authenticate a locally stored user. If successful then a user role mapping is performed.
     */
    public boolean loginLocalUser(@NonNull final String login, @NonNull final String password) {
        Optional<User> user = userRepository.findUserByLogin(login);
        if (!user.isPresent()) {
            return false;
        }

        if (passwordEncoder.matches(password, user.get().getPassword())) {
            mapUserRoles(user.get().getId(), login, login);

            user.get().setLastLogin(Instant.now());
            userRepository.save(user.get());

            return true;
        }
        return false;
    }

    private void mapUserRoles(@NonNull final Long userId, @NonNull final String userName, @NonNull final String login) {
        Collection<SimpleGrantedAuthority> authorities = userRolesMapper.mapUserRoles(login);

        // every successfully authorized user gets the role USER
        authorities.add(new SimpleGrantedAuthority(UserRolesMapper.ROLE_USER));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, "", authorities);
        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Logout a user.
     */
    public void logoutUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Check a user for a given role.
     */
    public boolean checkRole(@NonNull final String roleName) {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (roleName.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUserAuthenticated() {
        return (getAuthentication() != null) && !isRoleAnonymous();
    }

    public String getUserLogin() {
        if (isUserAuthenticated()) {
            return (String)getAuthentication().getPrincipal();
        }
        return "";
    }

    public User getUser() {
        if (isUserAuthenticated()) {
            Optional<User> user = userRepository.findById(getUserId());
            if (user.isPresent()) {
                return user.get();
            }
        }
        return null;
    }

    public long getUserId() {
        if (isUserAuthenticated()) {
            return (long)getAuthentication().getDetails();
        }
        return 0L;
    }

    public Set<Role> getUserRoles() {
        Set<Role> roles = new HashSet<>();
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            authentication.getAuthorities().forEach(authority -> {
                Role role = Role.fromString(authority.getAuthority());
                if (role != Role.ROLE_UNKNOWN) {
                    roles.add(role);
                }
            });
        }
        return roles;
    }

    public boolean isRoleAnonymous() {
        return checkRole(UserRolesMapper.ROLE_ANONYMOUS);
    }

    public boolean isRoleAdmin() {
        return checkRole(UserRolesMapper.ROLE_ADMIN);
    }

    public boolean isRoleUser() {
        return checkRole(UserRolesMapper.ROLE_USER);
    }

    public boolean isRoleAuthor() {
        return checkRole(UserRolesMapper.ROLE_AUTHOR);
    }

    public boolean isRoleTeamLead() {
        return checkRole(UserRolesMapper.ROLE_TEAM_LEAD);
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
