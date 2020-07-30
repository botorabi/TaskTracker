/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/navdrawer.dart';
import 'package:TaskTracker/widget/widget.login.dart';
import 'package:flutter/material.dart';


class PageHome extends StatefulWidget {
  PageHome({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _PageHomeState createState() => _PageHomeState();
}

class _PageHomeState extends State<PageHome> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: NavDrawer(),
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Welcome to Task Tracker',
              style: Theme.of(context).textTheme.headline4,
            ),
            Padding(
              padding: EdgeInsets.only(top: 20.0, bottom: 30),
              child: Text(
                  'Track and report your project activities.'
              ),
            ),
            Visibility(
              visible: (Config.authStatus.authenticated == false),
              child: WidgetLogin(),
            ),
           ],
        ),
      ),
    );
  }
}
