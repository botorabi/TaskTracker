/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.reportmail.configurationlist.dart';
import 'package:TaskTracker/widget/widget.tasklist.dart';
import 'package:TaskTracker/widget/widget.teamlist.dart';
import 'package:flutter/material.dart';


class PageTeamLead extends StatefulWidget {
  final String title;

  PageTeamLead({Key key, this.title}) : super(key: key);

  @override
  _PageTeamLeadState createState() => _PageTeamLeadState();
}

class _PageTeamLeadState extends State<PageTeamLead> {

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
            child: Container(
              constraints: BoxConstraints(maxWidth: Config.DEFAULT_PANEL_WIDTH),
              padding: const EdgeInsets.all(20.0),
              child:
                Column(
                  children: [
                    WidgetTeamList().setExpanded(true),
                    WidgetTaskList().setExpanded(false),
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
