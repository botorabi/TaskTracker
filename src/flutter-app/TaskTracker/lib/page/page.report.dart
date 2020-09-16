/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/widget/widget.report.dart';
import 'package:flutter/material.dart';

class PageReport extends StatefulWidget {
  final String title;

  PageReport({Key key, this.title}) : super(key: key);

  @override
  _PageReportState createState() => _PageReportState();
}

class _PageReportState extends State<PageReport> {

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
                  WidgetTeamReport().setExpanded(true),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
