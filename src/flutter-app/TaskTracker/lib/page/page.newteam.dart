/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.teamedit.dart';
import 'package:flutter/material.dart';

class PageNewTeam extends StatefulWidget {
  final String title;

  PageNewTeam({Key key, this.title}) : super(key: key);

  @override
  _PageNewTeamState createState() => _PageNewTeamState();
}

class _PageNewTeamState extends State<PageNewTeam> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                WidgetTeamEdit(teamId: 0)
              ],
            ),
          ),
        ],
      ),
    );
  }
}
