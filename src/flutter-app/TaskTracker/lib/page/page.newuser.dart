/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.useredit.dart';
import 'package:flutter/material.dart';

class PageNewUser extends StatefulWidget {
  PageNewUser({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _PageNewUserState createState() => _PageNewUserState();
}

class _PageNewUserState extends State<PageNewUser> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            WidgetUserEdit(userId: 0)
          ],
        ),
      ),
    );
  }
}
