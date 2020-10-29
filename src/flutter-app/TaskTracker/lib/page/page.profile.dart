/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.useredit.dart';
import 'package:flutter/material.dart';

class PageLogin extends StatefulWidget {
  final String title;

  PageLogin({Key key, this.title}) : super(key: key);

  @override
  _PageLoginState createState() => _PageLoginState();
}

class _PageLoginState extends State<PageLogin> {

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
            child: WidgetUserEdit(userId: Config.authStatus.userId),
          ),
        ],
      ),
    );
  }
}
