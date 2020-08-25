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
public interface TeamRepository extends CrudRepository<Team, Long> {

    Optional<Team> findTeamByName(@NonNull final String name);

    @Query("select new net.vrfun.tasktracker.user.TeamShortInfo(team) " +
            "from net.vrfun.tasktracker.user.Team team where " +
            "(team.name like concat('%',:filter,'%')) or (team.description like concat('%',:filter,'%'))")
    List<TeamShortInfo> searchTeam(@NonNull @Param("filter") final String filter);

    @Query("select team " +
            "from net.vrfun.tasktracker.user.Team team where :user member of team.users")
    List<Team> findUserTeams(@NonNull final User user);
}
