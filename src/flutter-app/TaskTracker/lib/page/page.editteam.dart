/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.teamedit.dart';
import 'package:flutter/material.dart';

class PageEditTeam extends StatefulWidget {
  PageEditTeam({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _PageEditTeamState createState() => _PageEditTeamState();
}

class _PageEditTeamState extends State<PageEditTeam> {

  @override
  Widget build(BuildContext context) {

    final int groupId = ModalRoute.of(context).settings.arguments;
    if (groupId == 0) {
      print("Invalid Page argument!");
      return Container();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            WidgetTeamEdit(teamId: groupId)
          ],
        ),
      ),
    );
  }
}
