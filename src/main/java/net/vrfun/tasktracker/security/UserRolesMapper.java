/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.security;

import net.vrfun.tasktracker.user.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Class for mapping user's roles (authorities)
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Component
public class UserRolesMapper {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Virtual role for anonymous users
     */
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    /**
     * Virtual role for successfully authenticated users
     */
    public static final String ROLE_USER = "ROLE_USER";

    public static final String ROLE_ADMIN = Role.ROLE_ADMIN.name();
    public static final String ROLE_AUTHOR = Role.ROLE_AUTHOR.name();
    public static final String ROLE_TEAM_LEAD = Role.ROLE_TEAM_LEAD.name();

    private UserRepository userRepository;

    @Bean
    public UserRolesMapper createUserRolesMapper() {
        return new UserRolesMapper();
    }

    public UserRolesMapper() {}

    @Autowired
    public UserRolesMapper(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<SimpleGrantedAuthority> mapUserRoles(@NonNull final String userLogin) {
        LOGGER.debug("mapping user {}", userLogin);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userRepository
                .findUserByLogin(userLogin)
                .ifPresent(u -> {
                    Collection<UserRole> roles = u.getRoles();
                    collectAuthorities(roles, authorities);
                });

        return authorities;
    }

    private void collectAuthorities(@Nullable final Collection<UserRole> roles, @NonNull Collection<SimpleGrantedAuthority> authorities) {
        if (roles == null) {
            return;
        }
        roles.forEach(role -> {
            switch(role.getRole()) {
                case ROLE_ADMIN:
                    authorities.add((new SimpleGrantedAuthority(ROLE_ADMIN)));
                    break;

                case ROLE_AUTHOR:
                    authorities.add(new SimpleGrantedAuthority(ROLE_AUTHOR));
                    break;

                case ROLE_TEAM_LEAD:
                    authorities.add(new SimpleGrantedAuthority(ROLE_TEAM_LEAD));
                    break;
            }
        });
    }
}
