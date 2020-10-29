/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/authstatus.dart';
import 'package:TaskTracker/service/service.login.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.sessiontimeout.dart';
import 'package:flutter/material.dart';

class NavDrawer extends StatelessWidget {

  final _serviceLogin = ServiceLogin();

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: <Widget>[
          DrawerHeader(
            child: Column(
              children: [
                SizedBox(height: 10.0),
                Text(
                  Config.appInfo.name,
                  style: TextStyle(color: Colors.white, fontSize: 25),
                  textAlign: TextAlign.left,
                ),
                SizedBox(height: 10.0),
                Text(
                  Translator.text('Common', 'Version') + " " + Config.appInfo.version,
                  style: TextStyle(color: Colors.white, fontSize: 16),
                  textAlign: TextAlign.left,
                ),
                SizedBox(height: 5.0),
                Visibility(
                  visible: (Config.authStatus.authenticated == true),
                  child:
                  Column(
                    children: [
                      Text(
                        Config.authStatus.loginName,
                        style: TextStyle(color: Colors.white, fontSize: 20),
                        textAlign: TextAlign.left,
                      ),
                    ]
                  ),
                ),
              ],
            ),
            decoration: BoxDecoration(
                color: Colors.blue,
            ),
          ),
          ListTile(
            leading: Icon(Icons.home),
            title: Text(Translator.text('NavDrawer', 'Welcome')),
            onTap: () {
              Navigator.of(context).pop();
              Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
            },
          ),
          ListTile(
            leading: Icon(Icons.language),
            title: Text(Translator.text('NavDrawer', 'Language')),
            onTap: () {
              Config.locale = (Config.locale == 'de') ? 'en' : 'de';
              Translator.setLocale(Config.locale);
              Navigator.of(context).pop();
              Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
            },
          ),
          Visibility(
            visible: (Config.authStatus.authenticated == false),
            child: ListTile(
              leading: Icon(Icons.exit_to_app),
              title: Text(Translator.text('NavDrawer', 'Login')),
              onTap: () {
                Navigator.of(context).pop();
                Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
              },
            ),
          ),
          Visibility(
            visible: Config.authStatus.authenticated,
            child: Column(
              children:[
                ListTile(
                  leading: Icon(Icons.face),
                  title: Text(Translator.text('NavDrawer', 'Profile')),
                  onTap: () {
                    Navigator.of(context).pop();
                    Navigator.pushNamed(context, NavigationLinks.NAV_PROFILE);
                  },
                ),
                Visibility(
                  visible: Config.authStatus.isAdmin(),
                  child: ListTile(
                    leading: Icon(Icons.settings),
                    title: Text(Translator.text('NavDrawer', 'Administration')),
                    onTap: () {
                      Navigator.of(context).pop();
                      Navigator.pushNamed(context, NavigationLinks.NAV_ADMIN);
                    },
                  ),
                ),
                Visibility(
                  visible: Config.authStatus.isTeamLead(),
                  child: ListTile(
                    leading: Icon(Icons.settings),
                    title: Text(Translator.text('NavDrawer', 'Team Management')),
                    onTap: () {
                      Navigator.of(context).pop();
                      Navigator.pushNamed(context, NavigationLinks.NAV_TEAM_LEAD);
                    },
                  ),
                ),
                Visibility(
                visible: (Config.authStatus.isTeamLead() || Config.authStatus.isAdmin()),
                  child: ListTile(
                    leading: Icon(Icons.insert_chart),
                    title: Text(Translator.text('Common', 'Progress Report')),
                    onTap: () {
                      Navigator.of(context).pop();
                      Navigator.pushNamed(context, NavigationLinks.NAV_REPORT);
                    },
                  ),
                ),
                ListTile(
                  leading: Icon(Icons.exit_to_app),
                  title: Text(Translator.text('NavDrawer', 'Logout')),
                  onTap: () => {
                    this._serviceLogin.logoutUser().then((result) {
                      Config.authStatus = AuthStatus();
                      Navigator.of(context).pop();
                      Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
                    }),
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
