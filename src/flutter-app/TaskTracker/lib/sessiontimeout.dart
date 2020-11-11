/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:async';
import 'dart:html';

import 'package:TaskTracker/service/authstatus.dart';
import 'package:flutter/material.dart';

import 'config.dart';
import 'navigation.links.dart';
import 'service/service.login.dart';

/// Logout user after long inactivity period.
class SessionTimeoutHandler {

  static const MAIN_CONTAINER_ID = 'tasktracker-main-content';

  static const _KEEP_ALIVE_TIME = 600;

  final GlobalKey<NavigatorState> _navigator;
  Timer _sessionTimer;
  Timer _keepAliveTimer;
  int   _timeoutInSeconds;

  static DateTime _timeOut;

  SessionTimeoutHandler(this._navigator, this._timeoutInSeconds);

  void installLogoutHandler() {
    var body = document.getElementById(MAIN_CONTAINER_ID);
    body.addEventListener("click", (event) => resetLogoutTimer());
    body.addEventListener("keyup", (event) => resetLogoutTimer());

    resetLogoutTimer();
  }

  /// Return the time left to logout in seconds.
  /// If user is not authenticated then 0 is returned.
  static int timeLeftInSeconds() {
    if ((_timeOut == null) || !Config.authStatus.authenticated) {
      return 0;
    }
    return ((_timeOut.millisecondsSinceEpoch - DateTime.now().millisecondsSinceEpoch) / 1000).floor();
  }

  /// Return a string representing the timeout in mm:ss format.
  static String timeLeftFormattedString() {
    int seconds = timeLeftInSeconds();
    int minutes = (seconds / 60).floor();
    return minutes.toString().padLeft(2, '0') + ':' + (seconds % 60).toString().padLeft(2, '0');
  }

  void resetLogoutTimer() {
    _timeOut = DateTime.now().add(Duration(seconds: _timeoutInSeconds));
    _sessionTimer?.cancel();
    _sessionTimer = Timer(Duration(seconds: _timeoutInSeconds), _logout);
    _keepAliveTimer?.cancel();
    _keepAliveTimer = Timer(Duration(seconds: _KEEP_ALIVE_TIME), _keepAlive);
  }

  void _logout() {
    if (Config.authStatus.authenticated) {
      ServiceLogin().logoutUser().then((result) {
        Config.authStatus = AuthStatus();
        _navigator.currentState.pushNamedAndRemoveUntil(
            NavigationLinks.NAV_HOME, (Route<dynamic> route) => false);
      });
    }
  }

  void _keepAlive() {
    ServiceLogin().getAppInfo();
  }
}
