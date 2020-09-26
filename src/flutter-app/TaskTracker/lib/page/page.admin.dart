/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/widget/widget.reportmail.configurationlist.dart';
import 'package:TaskTracker/widget/widget.tasklist.dart';
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
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Container(
              constraints: BoxConstraints(maxWidth: Config.defaultPanelWidth),
              padding: const EdgeInsets.all(20.0),
              child:
                Column(
                  children: [
                    WidgetTaskList().setExpanded(true),
                    WidgetTeamList().setExpanded(false),
                    WidgetUserList().setExpanded(false),
                    WidgetReportMailConfigurationList().setExpanded(false),
                  ],
                ),
              ),
            ),
          ],
        ),
    );
  }
}
