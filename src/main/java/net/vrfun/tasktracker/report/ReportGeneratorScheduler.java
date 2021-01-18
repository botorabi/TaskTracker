/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;


/**
 * Report job scheduler
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
public class ReportGeneratorScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final TaskScheduler taskScheduler;
    private final ReportGeneratorService reportGeneratorService;
    private final ReportMailConfigurationRepository reportMailConfigurationRepository;

    private final Map<Long, ScheduledFuture<?>> reportingJobs = new HashMap<>();

    @Autowired
    public ReportGeneratorScheduler(@NonNull final TaskScheduler taskScheduler,
                                    @NonNull final ReportGeneratorService reportGeneratorService,
                                    @NonNull final ReportMailConfigurationRepository reportMailConfigurationRepository) {

        this.taskScheduler = taskScheduler;
        this.reportGeneratorService = reportGeneratorService;
        this.reportMailConfigurationRepository = reportMailConfigurationRepository;
    }

    /**
     * Call this method on application start.
     */
    public void initializeSchedulers() {
        List<ReportMailConfiguration> configs = reportMailConfigurationRepository.findAll();
        configs.forEach(this::addOrUpdateReportingJob);
    }

    @NonNull
    public Map<Long, ScheduledFuture<?>> getReportingJobs() {
        return reportingJobs;
    }

    public void addOrUpdateReportingJob(@NonNull final ReportMailConfiguration configuration) {
        if (getReportingJobs().containsKey(configuration.getId())) {
            removeReportingJob(configuration.getId());
        }

        String cron = createCroneConfiguration(configuration);

        String scheduleTime = configuration.getReportPeriod().name() + ", Weekday: " +
                configuration.getReportWeekDay().name() + ", Time: " +
                configuration.getReportHour() + ":" +
                configuration.getReportMinute();

        LOGGER.info("(Re-)Scheduling report generation cron job '{}' ({}) at: {} (cron: {})",
                configuration.getName(), configuration.getId(), scheduleTime, cron);

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> reportGeneratorService.generateReport(configuration.getId()),
                new CronTrigger(cron, TimeZone.getTimeZone(TimeZone.getDefault().getID())));

       getReportingJobs().put(configuration.getId(), scheduledTask);
    }

    public void removeReportingJob(@NonNull final Long configurationID) {
        ScheduledFuture<?> scheduledTask = getReportingJobs().get(configurationID);
        if(scheduledTask != null) {
            scheduledTask.cancel(true);
            getReportingJobs().remove(configurationID);
            LOGGER.info("Report generation cron job was removed: {}", configurationID);
        }
    }

    protected String createCroneConfiguration(@NonNull final ReportMailConfiguration configuration) {
        // cron syntax: <second> <minute> <hour> <day-of-month> <month> <day-of-week> <year> <command>
        String cron = "0 " + configuration.getReportMinute() + " " + configuration.getReportHour();

        if (configuration.getReportPeriod() == ReportPeriod.PERIOD_MONTHLY) {
            cron += " 1-7 * ";
        }
        else if (configuration.getReportPeriod() == ReportPeriod.PERIOD_WEEKLY) {
            cron += " * * ";
        }
        else {
            LOGGER.error("Unsupported reporting period: {}", configuration.getReportPeriod());
        }

        cron += configuration.getReportWeekDay().toCronDay();
        return cron;
    }
}
