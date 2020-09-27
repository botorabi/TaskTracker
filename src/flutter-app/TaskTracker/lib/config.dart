/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:ui';

import 'package:TaskTracker/service/authstatus.dart';

class Config {

  /// Adapt the base URL if e.g. your server is behind a reverse proxy, or
  /// during the development set it to 'http://localhost:8080' in order to
  /// use Flutter's devtools server.
  static final String baseURL = '';

  static AuthStatus authStatus = AuthStatus();

  static final double defaultPanelWidth = 800;

  static final double defaultEditorWidth = 600;

  static final Color listBackgroundColor = Color.fromARGB(255, 250, 250, 250);

  static final Color listBorderColor = Color.fromARGB(255, 225, 225, 225);
}
