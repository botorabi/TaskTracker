/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.taskedit.dart';
import 'package:flutter/material.dart';

class PageNewTask extends StatefulWidget {
  final String title;

  PageNewTask({Key key, this.title}) : super(key: key);

  @override
  _PageNewTaskState createState() => _PageNewTaskState();
}

class _PageNewTaskState extends State<PageNewTask> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading: PageDrawer.buildNavigateBack(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                WidgetTaskEdit(taskId: 0)
              ],
            ),
          ),
        ],
      ),
    );
  }
}
