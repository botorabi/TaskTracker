/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:io';

import 'package:TaskTracker/config.dart';
import 'package:http/http.dart';

import 'authstatus.dart';

class ServiceLogin {

  static final Map<String, String> _httpHeaders = {
    'Content-type': 'application/json',
    'withCredentials': 'true'
  };

  Future<AuthStatus> getLoginStatus() async {
    Response response = await get(Config.baseURL + '/api/user/status',
                                  headers: _httpHeaders);

    if (response.statusCode == HttpStatus.ok) {
      return AuthStatus.fromJsonString(response.body);
    }
    else {
      return Future<AuthStatus>.error(response.statusCode);
    }
  }

  Future<AuthStatus> loginUser(final String login, final String password) async {
    Response response = await post(Config.baseURL + '/api/user/login',
                                   headers: _httpHeaders,
                                   body: '{"login": "' + login + '", "password": "' + password + '"}');

    if (response.statusCode == HttpStatus.ok) {
      return AuthStatus.fromJsonString(response.body);
    }
    else {
      return Future<AuthStatus>.error(response.statusCode);
    }
  }

  Future<bool> logoutUser() async {
    Response response = await get(Config.baseURL + '/api/user/logout',
                                  headers: _httpHeaders);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }
}
