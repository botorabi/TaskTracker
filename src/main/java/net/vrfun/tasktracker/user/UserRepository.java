/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByLogin(String login);

    /**
     * NOTE: There some trouble extracting the user roles in following queries! So we omit it and fetch the roles manually.
     */

    @Query("select new net.vrfun.tasktracker.user.UserDTO(user.id, user.realName, user.login, user.email, user.dateCreation, user.lastLogin) " +
            "from net.vrfun.tasktracker.user.User user order by user.realName")
    List<UserDTO> getUsers();

    @Query("select new net.vrfun.tasktracker.user.UserDTO(user.id, user.realName, user.login, user.email, user.dateCreation, user.lastLogin) " +
            "from net.vrfun.tasktracker.user.User user where user.realName like concat('%',:filter,'%') order by user.realName")
    List<UserDTO> searchUser(@NonNull @Param("filter") final String filter);

    @Query("select new net.vrfun.tasktracker.user.UserDTO(user.id, user.realName, user.login, user.email, user.dateCreation, user.lastLogin) " +
            "from net.vrfun.tasktracker.user.User user where user.id = :id")
    Optional<UserDTO> getUserById(@NonNull @Param("id") final Long id);

    @Query("select new net.vrfun.tasktracker.user.UserDTO(user.id, user.realName, user.login, user.email, user.dateCreation, user.lastLogin) " +
            "from net.vrfun.tasktracker.user.User user where user.login = :login")
    Optional<UserDTO> getUserByLogin(@NonNull @Param("login") final String login);

    @Query("select new net.vrfun.tasktracker.user.UserDTO(user.id, user.realName, user.login, user.email, user.dateCreation, user.lastLogin) " +
            "from net.vrfun.tasktracker.user.User user where user.ldapLogin = :ldapLogin")
    Optional<UserDTO> getUserByLdapLogin(@NonNull @Param("ldapLogin") final String ldapLogin);
}
