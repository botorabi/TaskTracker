/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
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

    @Query("select team from net.vrfun.tasktracker.user.Team team order by team.name")
    List<Team> findAll();

    @Query("select team from net.vrfun.tasktracker.user.Team team where team.id in :teamIDs order by team.name")
    List<Team> findAllById(@NonNull final List<Long> teamIDs);

    @Query("select new net.vrfun.tasktracker.user.TeamDTO(team) " +
            "from net.vrfun.tasktracker.user.Team team where " +
            "(team.name like concat('%',:filter,'%')) or (team.description like concat('%',:filter,'%')) order by team.name")
    List<TeamDTO> searchAllTeam(@NonNull @Param("filter") final String filter);

    @Query("select new net.vrfun.tasktracker.user.TeamDTO(team) " +
            "from net.vrfun.tasktracker.user.Team team where (:user member of team.users or :user member of team.teamLeaders) and " +
            "( (team.name like concat('%',:filter,'%')) or (team.description like concat('%',:filter,'%')) ) order by team.name")
    List<TeamDTO> searchUserTeams(@NonNull final User user, @NonNull @Param("filter") final String filter);

    @Query("select distinct team " +
            "from net.vrfun.tasktracker.user.Team team where :user member of team.users or :user member of team.teamLeaders")
    List<Team> findUserTeams(@NonNull final User user);

    @Query("select distinct team " +
            "from net.vrfun.tasktracker.user.Team team where :user member of team.teamLeaders order by team.name")
    List<Team> findTeamLeadTeams(@NonNull final User user);
}
