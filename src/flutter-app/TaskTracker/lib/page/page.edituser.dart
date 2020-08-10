/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/widget/widget.useredit.dart';
import 'package:flutter/material.dart';

class PageEditUser extends StatefulWidget {
  final String title;

  PageEditUser({Key key, this.title}) : super(key: key);

  @override
  _PageEditUserState createState() => _PageEditUserState();
}

class _PageEditUserState extends State<PageEditUser> {

  @override
  Widget build(BuildContext context) {

    final int userId = ModalRoute.of(context).settings.arguments;
    if (userId == 0) {
      print("Invalid Page argument!");
      return Container();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            WidgetUserEdit(userId: userId)
          ],
        ),
      ),
    );
  }
}
