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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * A collection of team related services.
 *
 * @author          boto
 * Creation Date    August 2020
 */
@Service
public class Teams {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private TeamRepository teamRepository;
    private UserRepository userRepository;

    @Autowired
    public Teams(@NonNull final TeamRepository teamRepository,
                 @NonNull final UserRepository userRepository) {

        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @NonNull
    public Team createTeam(@NonNull final ReqTeamEdit reqTeamEdit) throws IllegalArgumentException {
        if (StringUtils.isEmpty(reqTeamEdit.getName())) {
            throw new IllegalArgumentException("A team needs at least a name!");
        }

        Optional<Team> foundTeam = teamRepository.findTeamByName(reqTeamEdit.getName());
        if (foundTeam.isPresent()) {
            throw new IllegalArgumentException("A team with given name already exists!");
        }

        Team team = new Team(
                reqTeamEdit.getName().trim(),
                (reqTeamEdit.getDescription() != null) ? reqTeamEdit.getDescription().trim() : null
        );

        return teamRepository.save(team);
    }

    @NonNull
    public Team editTeam(@NonNull final ReqTeamEdit reqTeamEdit) throws IllegalArgumentException {
        Optional<Team> foundTeam = teamRepository.findTeamByName(reqTeamEdit.getName());

        if (!foundTeam.isPresent()) {
            throw new IllegalArgumentException("A team with given name does not exist!");
        }

        if (!StringUtils.isEmpty(reqTeamEdit.getName())) {
            foundTeam.get().setName(reqTeamEdit.getName().trim());
        }

        if (!StringUtils.isEmpty(reqTeamEdit.getDescription())) {
            foundTeam.get().setDescription(reqTeamEdit.getDescription().trim());
        }

        if (reqTeamEdit.getActive() != null) {
            foundTeam.get().setActive(reqTeamEdit.getActive());
        }

        if (reqTeamEdit.getUsers() != null) {
            setTeamUsers(reqTeamEdit, foundTeam.get());
        }

        return teamRepository.save(foundTeam.get());
    }

    private void setTeamUsers(@NonNull final ReqTeamEdit reqTeamEdit, @NonNull Team team) {
        Collection<User> users = new ArrayList<>();
        reqTeamEdit.getUsers()
                .stream()
                .forEach((userID) -> {
                    Optional<User> user = userRepository.findById(userID);
                    if (user.isPresent()) {
                        users.add(user.get());
                    }
                    else {
                        LOGGER.warn("Cannot add user with ID '{}' to team '{}', user does not exist!",
                                userID, reqTeamEdit.getName());

                        throw new IllegalArgumentException("Cannot add user with ID '" + userID + "' to team '" +
                                reqTeamEdit.getName() + "', user does not exist!");
                    }
                }
            );

        team.setUsers(users);
    }

    public void deleteTeam(Long id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Invalid ID");
        }

        Optional<Team> foundTeam = teamRepository.findById(id);

        if (!foundTeam.isPresent()) {
            throw new IllegalArgumentException("A team with given ID does not exist!");
        }

        teamRepository.delete(foundTeam.get());
    }

    @NonNull
    public List<TeamShortInfo> getTeams() {
        List<TeamShortInfo> teams = new ArrayList<>();
        teamRepository.findAll().forEach((team) -> teams.add(new TeamShortInfo(team)));
        return teams;
    }

    @NonNull
    public TeamShortInfo getTeamById(Long id) throws IllegalArgumentException {
        Optional<Team> foundTeam = teamRepository.findById(id);
        if (foundTeam.isEmpty()) {
            throw new IllegalArgumentException("Team with ID '" + id + "' does not exist!");
        }
        return new TeamShortInfo(foundTeam.get());
    }
}
