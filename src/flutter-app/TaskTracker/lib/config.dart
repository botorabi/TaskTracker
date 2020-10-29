/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:ui';

import 'package:TaskTracker/service/appinfo.dart';
import 'package:TaskTracker/service/authstatus.dart';

class Config {

  static AuthStatus authStatus = AuthStatus();

  static AppInfo appInfo = AppInfo();

  /// Timeout in seconds for automatic logout on inactivity
  static const LOGOUT_TIMEOUT = 30 * 60;

  static String locale = 'de';

  /// Adapt the base URL if e.g. your server is behind a reverse proxy, or
  /// during the development set it to 'http://localhost:8080' in order to
  /// use Flutter's devtools server.
  static const String BASE_URL = 'http://localhost:8080';

  static const double DEFAULT_PANEL_WIDTH = 800;

  static const double DEFAULT_EDITOR_WIDTH = 600;

  static const Color LIST_BACKGROUND_COLOR = Color.fromARGB(255, 250, 250, 250);

  static const Color LIST_BORDER_COLOR = Color.fromARGB(255, 225, 225, 225);
}
