/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface ReportMailConfigurationRepository extends CrudRepository<ReportMailConfiguration, Long> {

    List<ReportMailConfiguration> findAll();

    @Query("select configuration " +
            "from net.vrfun.tasktracker.report.ReportMailConfiguration configuration where :teamLead member of configuration.reportingTeams.teamLeaders")
    List<ReportMailConfiguration> findTeamLeadConfigurations(@NonNull final User teamLead);

    @Query("select configuration " +
            "from net.vrfun.tasktracker.report.ReportMailConfiguration configuration where " +
            ":teamLead member of configuration.reportingTeams.teamLeaders and " +
            ":id = configuration.id")
    Optional<ReportMailConfiguration> findTeamLeadConfiguration(@NonNull final User teamLead, @NonNull final @Param("id") Long id);
}
