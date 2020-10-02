/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import org.springframework.lang.NonNull;

public enum ReportPeriod {
    PERIOD_UNKNOWN,
    PERIOD_WEEKLY,
    PERIOD_MONTHLY;

    public final static String PERIOD_NAME_UNKNOWN = "PERIOD_UNKNOWN";
    public final static String PERIOD_NAME_WEEKLY  = "PERIOD_WEEKLY";
    public final static String PERIOD_NAME_MONTHLY = "PERIOD_MONTHLY";

    public static ReportPeriod fromString(@NonNull final String reportPeriod) {
        try {
            return valueOf(reportPeriod);
        }
        catch(Throwable throwable) {}
        return PERIOD_UNKNOWN;
    }
}
