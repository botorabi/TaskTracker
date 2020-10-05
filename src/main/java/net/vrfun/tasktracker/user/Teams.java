/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import net.vrfun.tasktracker.report.ReportMailConfigurationDTO;
import net.vrfun.tasktracker.security.UserAuthenticator;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private final UserAuthenticator userAuthenticator;

    @Autowired
    public Teams(@NonNull final TeamRepository teamRepository,
                 @NonNull final UserRepository userRepository,
                 @NonNull final UserAuthenticator userAuthenticator) {

        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userAuthenticator = userAuthenticator;
    }

    @NonNull
    public Team createTeam(@NonNull final ReqTeamEdit reqTeamEdit) {
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

        if (reqTeamEdit.getUserIDs() != null) {
            setTeamUsers(reqTeamEdit, team);
        }

        if (reqTeamEdit.getTeamLeaderIDs() != null) {
            setTeamLeaders(reqTeamEdit, team);
        }

        return teamRepository.save(team);
    }

    @NonNull
    public Team editTeam(@NonNull final ReqTeamEdit reqTeamEdit) {
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

        if (reqTeamEdit.getUserIDs() != null) {
            setTeamUsers(reqTeamEdit, foundTeam.get());
        }

        if (reqTeamEdit.getTeamLeaderIDs() != null) {
            setTeamLeaders(reqTeamEdit, foundTeam.get());
        }

        return teamRepository.save(foundTeam.get());
    }

    protected void setTeamUsers(@NonNull final ReqTeamEdit reqTeamEdit, @NonNull Team team) {
        Collection<User> users = new ArrayList<>();
        reqTeamEdit.getUserIDs()
                .stream()
                .forEach((userID) -> {
                    Optional<User> user = userRepository.findById(userID);
                    user.ifPresentOrElse(
                            (foundUser) -> users.add(foundUser),
                            () -> LOGGER.warn("Cannot add user with ID '{}' to team '{}', user does not exist!",
                                    userID, reqTeamEdit.getName()));
                    }
                );

        team.setUsers(users);
    }

    protected void setTeamLeaders(@NonNull final ReqTeamEdit reqTeamEdit, @NonNull Team team) {
        Collection<User> teamLeaders = new ArrayList<>();
        reqTeamEdit.getTeamLeaderIDs()
                .stream()
                .forEach((userID) -> {
                            Optional<User> user = userRepository.findById(userID);
                            user.ifPresentOrElse(
                                    (foundUser) -> teamLeaders.add(foundUser),
                                    () -> LOGGER.warn("Cannot add team leader with ID '{}' to team '{}', user does not exist!",
                                            userID, reqTeamEdit.getName()));
                        }
                );

        team.setTeamLeaders(teamLeaders);
    }

    public void deleteTeam(Long id) {
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
    public List<TeamDTO> getTeams() {
        List<TeamDTO> teams = new ArrayList<>();
        if (userAuthenticator.isRoleAdmin()) {
            teamRepository.findAll().forEach((team) -> teams.add(new TeamDTO(team)));
        } else if (userAuthenticator.isRoleTeamLead()) {
            teamRepository.findTeamLeadTeams(userAuthenticator.getUser()).forEach((team) -> teams.add(new TeamDTO(team)));
        }
        else {
            throw new IllegalArgumentException("Unauthorized access to teams");
        }
        return teams;
    }

    @NonNull
    public TeamDTO getTeamById(Long id) {
        Optional<Team> foundTeam = teamRepository.findById(id);
        if (foundTeam.isEmpty()) {
            throw new IllegalArgumentException("Team with ID '" + id + "' does not exist!");
        }
        if (userAuthenticator.isRoleAdmin()) {
            return new TeamDTO(foundTeam.get());
        } else if (userAuthenticator.isRoleTeamLead()) {
            for (User teamLead: foundTeam.get().getTeamLeaders()) {
                if (teamLead.getId().equals(userAuthenticator.getUserId())) {
                    return new TeamDTO(foundTeam.get());
                }
            }
            throw new IllegalArgumentException("Unauthorized access to team, not your Team!");
        }
        else {
            throw new IllegalArgumentException("Unauthorized access to team");
        }
    }

    @NonNull
    public List<TeamDTO> searchTeams(@NonNull final String filter) {
        if (userAuthenticator.isRoleAdmin()) {
            return teamRepository.searchAllTeam(filter);
        } else {
            return teamRepository.searchUserTeams(userAuthenticator.getUser(), filter);
        }
    }
}
