/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/service/authstatus.dart';

class Config {

  /// Adapt the base URL if e.g. your server is behind a proxy
  static final String baseURL = '';

  static AuthStatus authStatus = AuthStatus();

  static final double defaultPanelWidth = 650;
}
