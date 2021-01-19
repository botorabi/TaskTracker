/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.progressedit.dart';
import 'package:flutter/material.dart';

class PageEditProgress extends StatefulWidget {
  final String title;

  PageEditProgress({Key key, this.title}) : super(key: key);

  @override
  _PageEditProgressState createState() => _PageEditProgressState();
}

class _PageEditProgressState extends State<PageEditProgress> {

  @override
  Widget build(BuildContext context) {

    final int progressId = ModalRoute.of(context).settings.arguments;
    if (progressId == 0) {
      print("Invalid Page argument!");
      return Container();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading: PageDrawer.buildNavigateBack(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: Center(
        child: ListView(
          shrinkWrap: true,
          children: [
            Center(
              child: WidgetProgressEdit(progressId: progressId),
            ),
          ],
        ),
      ),
    );
  }
}
