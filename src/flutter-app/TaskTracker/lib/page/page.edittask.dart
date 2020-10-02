/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.taskedit.dart';
import 'package:flutter/material.dart';

class PageEditTask extends StatefulWidget {
  final String title;

  PageEditTask({Key key, this.title}) : super(key: key);

  @override
  _PageEditTaskState createState() => _PageEditTaskState();
}

class _PageEditTaskState extends State<PageEditTask> {

  @override
  Widget build(BuildContext context) {

    final int taskId = ModalRoute.of(context).settings.arguments;
    if (taskId == 0) {
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
              child: WidgetTaskEdit(taskId: taskId),
            ),
          ],
        ),
      ),
    );
  }
}
