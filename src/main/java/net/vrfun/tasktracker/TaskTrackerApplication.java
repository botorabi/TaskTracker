/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker;

import net.vrfun.tasktracker.report.ReportGeneratorScheduler;
import net.vrfun.tasktracker.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskTrackerApplication {

    private final Users users;
    private final ReportGeneratorScheduler reportGeneratorScheduler;

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerApplication.class, args);
    }

    @Autowired
    public TaskTrackerApplication(@NonNull final Users users,
                                  @NonNull final ReportGeneratorScheduler reportGeneratorScheduler) {
        this.users = users;
        this.reportGeneratorScheduler = reportGeneratorScheduler;
    }

    /**
     * Put all start-up code to this method.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        users.setupApplicationUsers();
        reportGeneratorScheduler.initializeSchedulers();
    }
}
