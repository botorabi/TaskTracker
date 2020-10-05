/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/page.editprogress.dart';
import 'package:TaskTracker/page/page.editreportconfiguration.dart';
import 'package:TaskTracker/page/page.edittask.dart';
import 'package:TaskTracker/page/page.editteam.dart';
import 'package:TaskTracker/page/page.newprogress.dart';
import 'package:TaskTracker/page/page.newreportconfiguration.dart';
import 'package:TaskTracker/page/page.newtask.dart';
import 'package:TaskTracker/page/page.newteam.dart';
import 'package:TaskTracker/page/page.report.dart';
import 'package:TaskTracker/page/page.teamlead.dart';
import 'package:TaskTracker/page/page.viewprogress.dart';
import 'package:TaskTracker/service/appinfo.dart';
import 'package:TaskTracker/service/authstatus.dart';
import 'package:flutter/material.dart';

import 'config.dart';
import 'navigation.links.dart';
import 'page/page.admin.dart';
import 'page/page.edituser.dart';
import 'page/page.home.dart';
import 'page/page.newuser.dart';
import 'page/page.profile.dart';
import 'service/service.login.dart';

class StartApp {
  final _serviceLogin = ServiceLogin();

  void run() {
    _updateAuthStatusAndStart();
    _updateAppInfo();
  }

  void _updateAuthStatusAndStart() {
    this._serviceLogin.getLoginStatus().then((authStatus) {
      Config.authStatus = authStatus;
      runApp(AppTaskTracker());
    },
    onError: (err) {
      runApp(AppTaskTracker());
      Config.authStatus = AuthStatus();
    });
  }

  void _updateAppInfo() {
    this._serviceLogin.getAppInfo().then((appInfo) {
      Config.appInfo = appInfo;
    },
    onError: (err) {
      Config.appInfo = AppInfo();
    });
  }
}

void main() {
  StartApp app = StartApp();
  app.run();
}

class AppTaskTracker extends StatelessWidget {

  @override
  Widget build(BuildContext context) {

    return MaterialApp(
        title: 'Task Tracker',
        theme: ThemeData(
          primarySwatch: Colors.blue,
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        routes: {
          NavigationLinks.NAV_HOME            : (context) => PageHome(title: 'Task Tracker'),
          NavigationLinks.NAV_PROFILE         : (context) => PageLogin(title: 'User Profile'),
          NavigationLinks.NAV_ADMIN           : (context) => PageAdmin(title: 'Administration'),
          NavigationLinks.NAV_TEAM_LEAD       : (context) => PageTeamLead(title: 'Team Management'),
          NavigationLinks.NAV_NEW_USER        : (context) => PageNewUser(title: 'Create New User'),
          NavigationLinks.NAV_EDIT_USER       : (context) => PageEditUser(title: 'Edit User'),
          NavigationLinks.NAV_NEW_TEAM        : (context) => PageNewTeam(title: 'Create New Team'),
          NavigationLinks.NAV_EDIT_TEAM       : (context) => PageEditTeam(title: 'Edit Team'),
          NavigationLinks.NAV_NEW_TASK        : (context) => PageNewTask(title: 'Create New Task'),
          NavigationLinks.NAV_EDIT_TASK       : (context) => PageEditTask(title: 'Edit Task'),
          NavigationLinks.NAV_VIEW_PROGRESS   : (context) => PageViewProgress(title: 'View Progress'),
          NavigationLinks.NAV_NEW_PROGRESS    : (context) => PageNewProgress(title: 'Create New Progress Entry'),
          NavigationLinks.NAV_EDIT_PROGRESS   : (context) => PageEditProgress(title: 'Edit Progress Entry'),
          NavigationLinks.NAV_REPORT          : (context) => PageReport(title: 'Progress Report'),
          NavigationLinks.NAV_NEW_REPORT_CFG  : (context) => PageNewReportConfiguration(title: 'Create New Report Configuration'),
          NavigationLinks.NAV_EDIT_REPORT_CFG : (context) => PageEditReportConfiguration(title: 'Edit Report Configuration'),
        },
      );
  }
}
