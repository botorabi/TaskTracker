/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class AppInfo {
  String name = '';
  String version = '';

  AppInfo();

  factory AppInfo.fromJsonString(final String jsonString) {
    final info = AppInfo();
    Map<String, dynamic> fields = jsonDecode(jsonString);
    info.name = fields['name'];
    info.version = fields['version'];
    return info;
  }
}
