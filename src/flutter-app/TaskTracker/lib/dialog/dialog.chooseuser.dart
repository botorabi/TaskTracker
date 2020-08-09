/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';

class DialogChooseUser {

  BuildContext _context;
  List<UserInfo> _chosenUsers = List<UserInfo>();

  DialogChooseUser(this._context);

  Future<void> show(String title, String text, List<UserInfo> users) {
    return showDialog<void>(
        context: _context,
        barrierDismissible: true,
        builder: (_) =>
        new AlertDialog(
          title: Text(title),
          content: _createUserList(text, users),
          actions: <Widget>[
            FlatButton(
              child: Text(ButtonID.CANCEL),
              onPressed: () {
                Navigator.of(_context).pop(ButtonID.CANCEL);
              },
            ),
            FlatButton(
              child: Text(ButtonID.CHOOSE),
              onPressed: () {
                Navigator.of(_context).pop(_chosenUsers);
              },
            ),
          ],
        )
    );
  }

  Container _createUserList(String text, List<UserInfo> users) {
    return Container(
      child: Column(
        children: [
          Text(text),
// TODO create a list with users
        ],
      )
    );
  }
}
