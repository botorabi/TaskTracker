/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

abstract class CalendarUtils {

  static int getCalendarWeek(DateTime dateTime) {
    final days = dateTime.difference(DateTime(dateTime.year)).inDays;
    return ((days + 1) / 7).ceil();
  }

  static int getCalendarYear(DateTime dateTime) {
    return dateTime.year;
  }

  static int getCurrentCalendarWeek() {
    return getCalendarWeek(DateTime.now());
  }

  static int getCurrentCalendarYear() {
    return getCalendarYear(DateTime.now());
  }

  static bool checkCurrentWeekDistance(int reportWeek, int reportYear) {
    const MAX_CALENDAR_WEEKS = 53;
    const MAX_CALENDAR_WEEK_DISTANCE = 4;

    int currentWeek = getCurrentCalendarWeek();
    int currentYear = getCurrentCalendarYear();

    if ((currentYear - reportYear).abs() > 1) {
      return false;
    }

    if (currentYear == reportYear) {
      return ((reportWeek - currentWeek).abs() <= MAX_CALENDAR_WEEK_DISTANCE);
    }
    else if (currentYear < reportYear) {
      return (MAX_CALENDAR_WEEKS - currentWeek + reportWeek) <= MAX_CALENDAR_WEEK_DISTANCE;
    }
    else { // currentYear > reportYear
      return (MAX_CALENDAR_WEEKS + currentWeek - reportWeek) <= MAX_CALENDAR_WEEK_DISTANCE;
    }
  }
}
