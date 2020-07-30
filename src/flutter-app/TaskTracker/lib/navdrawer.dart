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
                Text(
                  'Menu',
                  style: TextStyle(color: Colors.white, fontSize: 25),
                ),
                Text(
                  Config.authStatus.loginName,
                  style: TextStyle(color: Colors.white, fontSize: 20),
                ),
              ],
            ),
            decoration: BoxDecoration(
                color: Colors.green,
                image: DecorationImage(
                    fit: BoxFit.fill,
                    image: AssetImage('images/cover.jpg'),
                )
            ),
          ),
          ListTile(
            leading: Icon(Icons.home),
            title: Text('Welcome'),
            onTap: () {
              Navigator.of(context).pop();
              Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
            },
          ),
          Visibility(
            visible: (Config.authStatus.authenticated == false),
            child: ListTile(
              leading: Icon(Icons.exit_to_app),
              title: Text('Login'),
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
                  title: Text('Profile'),
                  onTap: () {
                    Navigator.of(context).pop();
                    Navigator.pushNamed(context, NavigationLinks.NAV_PROFILE);
                  },
                ),
                Visibility(
                  visible: Config.authStatus.isAdmin(),
                  child: ListTile(
                    leading: Icon(Icons.settings),
                    title: Text('Administration'),
                    onTap: () {
                      Navigator.of(context).pop();
                      Navigator.pushNamed(context, NavigationLinks.NAV_ADMIN);
                    },
                  ),
                ),
                ListTile(
                  leading: Icon(Icons.exit_to_app),
                  title: Text('Logout'),
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
