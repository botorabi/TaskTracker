/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import org.springframework.lang.NonNull;

public enum ReportWeekDay {
    WEEKDAY_UNKNOWN,
    WEEKDAY_MONDAY,
    WEEKDAY_TUESDAY,
    WEEKDAY_WEDNESDAY,
    WEEKDAY_THURSDAY,
    WEEKDAY_FRIDAY,
    WEEKDAY_SATURDAY,
    WEEKDAY_SUNDAY;

    public final static String WEEKDAY_NAME_UNKNOWN   = "WEEKDAY_UNKNOWN";
    public final static String WEEKDAY_NAME_MONDAY    = "WEEKDAY_MONDAY";
    public final static String WEEKDAY_NAME_TUESDAY   = "WEEKDAY_TUESDAY";
    public final static String WEEKDAY_NAME_WEDNESDAY = "WEEKDAY_WEDNESDAY";
    public final static String WEEKDAY_NAME_THURSDAY  = "WEEKDAY_THURSDAY";
    public final static String WEEKDAY_NAME_FRIDAY    = "WEEKDAY_FRIDAY";
    public final static String WEEKDAY_NAME_SATURDAY  = "WEEKDAY_SATURDAY";
    public final static String WEEKDAY_NAME_SUNDAY    = "WEEKDAY_SUNDAY";

    @NonNull
    public static ReportWeekDay fromString(@NonNull final String reportWeekDay) {
        try {
            return valueOf(reportWeekDay);
        }
        catch(Throwable throwable) {}
        return WEEKDAY_UNKNOWN;
    }

    @NonNull
    public String toCronDay() {
        final int prefixLength = new String("WEEKDAY_").length();
        return name().substring(prefixLength, prefixLength + 3);
    }
}
