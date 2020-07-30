/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class AuthStatus {
  bool authenticated = false;
  int userId;
  String loginName = '';
  List<String> roles = [];

  AuthStatus();

  bool isAdmin() {
    return ((roles != null) && roles.contains("ROLE_ADMIN"));
  }

  factory AuthStatus.fromJsonString(final String jsonString) {
    AuthStatus status = AuthStatus();
    Map<String, dynamic> fields = jsonDecode(jsonString);
    status.userId = fields['id'] as int;
    status.authenticated = fields['authenticated'];
    status.loginName = fields['name'];
    status.roles = List.from(fields['roles']);
    return status;
  }
}
