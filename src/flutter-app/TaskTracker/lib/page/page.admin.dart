/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.teamedit.dart';
import 'package:TaskTracker/widget/widget.teamlist.dart';
import 'package:TaskTracker/widget/widget.userlist.dart';
import 'package:flutter/material.dart';


class PageAdmin extends StatefulWidget {
  final String title;

  PageAdmin({Key key, this.title}) : super(key: key);

  @override
  _PageAdminState createState() => _PageAdminState();
}

class _PageAdminState extends State<PageAdmin> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: [
            WidgetUserList().setExpanded(true),
            WidgetTeamList().setExpanded(false),
          ],
        ),
      ),
    );
  }
}
