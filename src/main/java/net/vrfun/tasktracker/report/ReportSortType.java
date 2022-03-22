/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import org.springframework.lang.NonNull;

public enum ReportSortType {
    REPORT_SORT_TYPE_TASK,
    REPORT_SORT_TYPE_USER,
    REPORT_SORT_TYPE_WEEK,
    REPORT_SORT_TYPE_TEAM,
    REPORT_SORT_TYPE_NONE;


    public final static String REPORT_SORT_TYPE_TASK_NAME = "REPORT_SORT_TYPE_TASK";
    public final static String REPORT_SORT_TYPE_USER_NAME = "REPORT_SORT_TYPE_USER";
    public final static String REPORT_SORT_TYPE_WEEK_NAME = "REPORT_SORT_TYPE_WEEK";
    public final static String REPORT_SORT_TYPE_TEAM_NAME = "REPORT_SORT_TYPE_TEAM";
    public final static String REPORT_SORT_TYPE_NONE_NAME = "REPORT_SORT_TYPE_NONE";

    @NonNull
    public static ReportSortType fromString(@NonNull final String reportWeekDay) {
        try {
            return valueOf(reportWeekDay);
        } catch(Throwable throwable) {
            return REPORT_SORT_TYPE_NONE;
        }
    }
}
