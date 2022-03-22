/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.report.dart';
import 'package:flutter/material.dart';

class PageReport extends StatelessWidget {
  final String title;
  final bool isTeamReport;
  
  PageReport({Key key, this.title, this.isTeamReport = false}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        leading: PageDrawer.buildNavigateBack(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Container(
              constraints: BoxConstraints(maxWidth: Config.DEFAULT_PANEL_WIDTH),
              padding: const EdgeInsets.all(20.0),
              child: WidgetReport(title: Translator.text('Common', 'Progress Report'), isTeam : isTeamReport),
            ),
          ),
        ],
      ),
    );
  }
}
