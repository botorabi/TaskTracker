/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.progressedit.dart';
import 'package:flutter/material.dart';

class PageNewProgress extends StatefulWidget {
  final String title;

  PageNewProgress({Key key, this.title}) : super(key: key);

  @override
  _PageNewProgressState createState() => _PageNewProgressState();
}

class _PageNewProgressState extends State<PageNewProgress> {

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
            child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                WidgetProgressEdit(progressId: 0)
              ],
            ),
          ),
        ],
      ),
    );
  }
}
