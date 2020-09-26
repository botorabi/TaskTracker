/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.reportmail.configurationedit.dart';
import 'package:flutter/material.dart';

class PageEditReportConfiguration extends StatefulWidget {
  final String title;

  PageEditReportConfiguration({Key key, this.title}) : super(key: key);

  @override
  _PageEditReportConfigurationState createState() => _PageEditReportConfigurationState();
}

class _PageEditReportConfigurationState extends State<PageEditReportConfiguration> {

  @override
  Widget build(BuildContext context) {

    final int configurationId = ModalRoute.of(context).settings.arguments;
    if (configurationId == 0) {
      print("Invalid Page argument!");
      return Container();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: ListView(
          shrinkWrap: true,
          children: [
            Center(
              child: WidgetReportConfigurationEdit(configurationId: configurationId),
            ),
          ],
        ),
      ),
    );
  }
}
