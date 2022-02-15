/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.TeamRepository;
import net.vrfun.tasktracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;




public class ReportsTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ReportMailConfigurationRepository reportMailConfigurationRepository;
    @Mock
    private ReportGeneratorScheduler reportGeneratorScheduler;
    @Mock
    private UserAuthenticator userAuthenticator;

    private Reports reports;
    private ReportCommonTest reportCommonTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        reports = new Reports(
                userRepository,
                teamRepository,
                reportMailConfigurationRepository,
                reportGeneratorScheduler,
                userAuthenticator);

        reportCommonTest = new ReportCommonTest();
    }

    @Test
    public void validateUserAccessAsAdmin() {
        doReturn(true).when(userAuthenticator).isRoleAdmin();

        assertThat(reports.validateUserAccess(new ArrayList<>())).isTrue();
    }

    @Test
    public void validateUserAccessAsTeamLead() {
        List<Team> teamIDs = Arrays.asList(createTeam(10L), createTeam(20L));

        doReturn(false).when(userAuthenticator).isRoleAdmin();
        doReturn(teamIDs).when(teamRepository).findTeamLeadTeams(any());

        assertThat(reports.validateUserAccess(Arrays.asList(10L, 20L))).isTrue();
        assertThat(reports.validateUserAccess(Arrays.asList(10L))).isTrue();
        assertThat(reports.validateUserAccess(Arrays.asList(20L))).isTrue();
        assertThat(reports.validateUserAccess(Arrays.asList(30L))).isFalse();
    }

    @NonNull
    private Team createTeam(long id) {
        Team team = new Team("My Team " + id, "Team Description " + id);
        team.setId(id);
        return team;
    }

    @Test
    public void validateNewReportMailConfiguration() {
        ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);

        assertAll(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration));
    }

    @Test
    public void validateNewReportMailConfigurationInvalidInput() {
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setName("");
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setMailSenderName(null);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setMailSubject(null);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportingTeams(Sets.newSet());
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportPeriod("Invalid Period");
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportWeekDay("Invalid Week Day");
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportHour(30L);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportHour(-1L);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportMinute(60L);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
        {
            final ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(10L);
            reqReportMailConfiguration.setReportMinute(-1L);
            assertThatThrownBy(() -> reports.validateNewReportMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void createMailConfiguration() {
        ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(20L);

        reports.createMailConfiguration(reqReportMailConfiguration);

        verify(reportMailConfigurationRepository, times(1)).save(any());

        verify(reportGeneratorScheduler, times(1)).addOrUpdateReportingJob(any());
    }

    @Test
    public void editMailConfigurationInvalidConfigurationId() {
        ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(20L);

        doReturn(Optional.empty()).when(reportMailConfigurationRepository).findById(20L);

        assertThatThrownBy(() -> reports.editMailConfiguration(reqReportMailConfiguration)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void editMailConfiguration() {
        ReqReportMailConfiguration reqReportMailConfiguration = reportCommonTest.createReqReportMailConfiguration(20L);

        doReturn(Optional.of(new ReportMailConfiguration())).when(reportMailConfigurationRepository).findById(20L);

        reports.editMailConfiguration(reqReportMailConfiguration);

        verify(reportMailConfigurationRepository, times(1)).save(any());

        verify(reportGeneratorScheduler, times(1)).addOrUpdateReportingJob(any());
    }

    @Test
    public void getReportMailConfigurationsAsAdmin() {
        List<ReportMailConfiguration> allConfigurations = createReportMailConfigurationInDB(Arrays.asList(100L, 200L));

        doReturn(allConfigurations).when(reportMailConfigurationRepository).findAll();
        doReturn(true).when(userAuthenticator).isRoleAdmin();

        assertThat(reports.getReportMailConfigurations().size()).isEqualTo(allConfigurations.size());
    }

    @NonNull
    private List<ReportMailConfiguration> createReportMailConfigurationInDB(@NonNull final List<Long> configurationIDs) {
        List<ReportMailConfiguration> configurations = new ArrayList<>();
        configurationIDs.forEach((id) -> {
            ReportMailConfiguration configuration = new ReportMailConfiguration();
            configuration.setId(id);
            configurations.add(configuration);
        });

        return configurations;
    }

    @Test
    public void getReportMailConfigurationsAsTeamLead() {
        List<ReportMailConfiguration> allConfigurations = createReportMailConfigurationInDB(Arrays.asList(100L, 200L));

        doReturn(allConfigurations).when(reportMailConfigurationRepository).findTeamLeadConfigurations(any());
        doReturn(true).when(userAuthenticator).isRoleTeamLead();

        assertThat(reports.getReportMailConfigurations().size()).isEqualTo(allConfigurations.size());
    }

    @Test
    public void getReportMailConfigurationsUnAuthorized() {
        doReturn(false).when(userAuthenticator).isRoleAdmin();
        doReturn(false).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.getReportMailConfigurations()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getReportMailConfigurationByIdAsAdmin() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findById(100L);
        doReturn(true).when(userAuthenticator).isRoleAdmin();

        assertThat(reports.getReportMailConfiguration(100L)).isNotNull();
    }

    @Test
    public void getReportMailConfigurationByIdAsTeamLead() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(true).when(userAuthenticator).isRoleTeamLead();

        assertThat(reports.getReportMailConfiguration(100L)).isNotNull();
    }

    @Test
    public void getReportMailConfigurationByIdUnauthorized() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(false).when(userAuthenticator).isRoleTeamLead();
        doReturn(false).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.getReportMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getReportMailConfigurationByIdInvalidId() {
        doReturn(Optional.empty()).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(true).when(userAuthenticator).isRoleAdmin();
        doReturn(false).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.getReportMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);

        doReturn(false).when(userAuthenticator).isRoleAdmin();
        doReturn(true).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.getReportMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void deleteReportMailConfigurationAsAdmin() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findById(100L);
        doReturn(true).when(userAuthenticator).isRoleAdmin();

        assertAll(() -> reports.deleteMailConfiguration(100L));
    }

    @Test
    public void deleteReportMailConfigurationAsTeamLead() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(true).when(userAuthenticator).isRoleTeamLead();

        assertAll(() -> reports.deleteMailConfiguration(100L));
    }

    @Test
    public void deleteReportMailConfigurationUnauthorized() {
        List<ReportMailConfiguration> configurations = createReportMailConfigurationInDB(Arrays.asList(100L));

        doReturn(Optional.of(configurations.get(0))).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(false).when(userAuthenticator).isRoleTeamLead();
        doReturn(false).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.deleteMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteReportMailConfigurationInvalidId() {
        doReturn(Optional.empty()).when(reportMailConfigurationRepository).findTeamLeadConfiguration(any(), any());
        doReturn(true).when(userAuthenticator).isRoleAdmin();
        doReturn(false).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.deleteMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);

        doReturn(false).when(userAuthenticator).isRoleAdmin();
        doReturn(true).when(userAuthenticator).isRoleTeamLead();

        assertThatThrownBy(() -> reports.deleteMailConfiguration(100L)).isInstanceOf(IllegalArgumentException.class);
    }
}
