/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

abstract class DateAndTimeFormatter {

  static String formatDate(DateTime dateTime) {
    if (dateTime == null) {
      return '';
    }
    return '' + dateTime.day.toString().padLeft(2, ' ') +
          '.' + dateTime.month.toString().padLeft(2, '0') +
          '.' + dateTime.year.toString();
  }

  static String formatTime(DateTime dateTime) {
    if (dateTime == null) {
      return '';
    }
    return '' + dateTime.hour.toString().padLeft(2, '0') +
          ':' + dateTime.minute.toString().padLeft(2, '0');
  }
}
