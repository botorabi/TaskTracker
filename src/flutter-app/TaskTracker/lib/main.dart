/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */


import 'package:TaskTracker/page/page.about.dart';
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
import 'package:TaskTracker/sessiontimeout.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

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
    this._serviceLogin.getLoginStatus().then((authStatus) {
      Config.authStatus = authStatus;
      _serviceLogin.getAppInfo().then((appInfo) {
        Config.appInfo = appInfo;
        runApp(AppTaskTracker());
      },
      onError: (err) {
        Config.appInfo = AppInfo();
      });
    },
    onError: (err) {
      runApp(AppTaskTracker());
      Config.authStatus = AuthStatus();
    });
  }
}

class AppTaskTracker extends StatefulWidget {
  @override
  _AppTaskTrackerState createState() => _AppTaskTrackerState();
}

class _AppTaskTrackerState extends State<AppTaskTracker> {

  final GlobalKey<NavigatorState> _navigator = GlobalKey<NavigatorState>();

  @override
  void initState() {
    super.initState();
    SessionTimeoutHandler(_navigator, Config.LOGOUT_TIMEOUT).installLogoutHandler();
  }

  @override
  Widget build(BuildContext context) {
    final shortcutsWithNoESC = Map.of(WidgetsApp.defaultShortcuts)
                                  ..remove(LogicalKeySet(LogicalKeyboardKey.escape));
    return MaterialApp(
      title: Translator.text('AppTaskTracker', 'Task Tracker'),
      shortcuts: shortcutsWithNoESC,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      navigatorKey: _navigator,
      routes: {
        NavigationLinks.NAV_HOME            : (context) => PageHome(title: Translator.text('AppTaskTracker', 'Task Tracker')),
        NavigationLinks.NAV_ABOUT           : (context) => PageAbout(title: Translator.text('AppTaskTracker', 'About')),
        NavigationLinks.NAV_PROFILE         : (context) => PageLogin(title: Translator.text('AppTaskTracker', 'User Profile')),
        NavigationLinks.NAV_ADMIN           : (context) => PageAdmin(title: Translator.text('AppTaskTracker', 'Administration')),
        NavigationLinks.NAV_TEAM_LEAD       : (context) => PageTeamLead(title: Translator.text('AppTaskTracker', 'Team Management')),
        NavigationLinks.NAV_NEW_USER        : (context) => PageNewUser(title: Translator.text('AppTaskTracker', 'Create New User')),
        NavigationLinks.NAV_EDIT_USER       : (context) => PageEditUser(title: Translator.text('AppTaskTracker', 'Edit User')),
        NavigationLinks.NAV_NEW_TEAM        : (context) => PageNewTeam(title: Translator.text('AppTaskTracker', 'Create New Team')),
        NavigationLinks.NAV_EDIT_TEAM       : (context) => PageEditTeam(title: Translator.text('AppTaskTracker', 'Edit Team')),
        NavigationLinks.NAV_NEW_TASK        : (context) => PageNewTask(title: Translator.text('AppTaskTracker', 'Create New Task')),
        NavigationLinks.NAV_EDIT_TASK       : (context) => PageEditTask(title: Translator.text('AppTaskTracker', 'Edit Task')),
        NavigationLinks.NAV_VIEW_PROGRESS   : (context) => PageViewProgress(title: Translator.text('AppTaskTracker', 'View Progress')),
        NavigationLinks.NAV_NEW_PROGRESS    : (context) => PageNewProgress(title: Translator.text('AppTaskTracker', 'Create New Progress Entry')),
        NavigationLinks.NAV_EDIT_PROGRESS   : (context) => PageEditProgress(title: Translator.text('AppTaskTracker', 'Edit Progress Entry')),
        NavigationLinks.NAV_TEAM_REPORT     : (context) => PageReport(title: Translator.text('Common', 'Progress Report'), isTeamReport: true),
        NavigationLinks.NAV_USER_REPORT     : (context) => PageReport(title: Translator.text('Common', 'Progress Report'), isTeamReport: false),
        NavigationLinks.NAV_NEW_REPORT_CFG  : (context) => PageNewReportConfiguration(title: Translator.text('AppTaskTracker', 'Create New Report Configuration')),
        NavigationLinks.NAV_EDIT_REPORT_CFG : (context) => PageEditReportConfiguration(title: Translator.text('AppTaskTracker', 'Edit Report Configuration')),
      },
    );
  }
}


void main() {
  StartApp app = StartApp();
  app.run();
}
