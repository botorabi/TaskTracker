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
import 'package:flutter/rendering.dart';

class DialogChooseUser {

  BuildContext _context;
  List<UserInfo> _chosenUsers = List<UserInfo>();

  DialogChooseUser(this._context);

  Future<void> show(String title, String text, List<UserInfo> users) {
    return showDialog<void>(
      context: _context,
      barrierDismissible: true,
      builder: (_) => AlertDialog(
        title: Text(title),
        //scrollable: true,
        content: _createUsersUI(text, users),
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

  SizedBox _createUsersUI(String text, List<UserInfo> users) {
    return SizedBox(
      height: 200,
      width: 300,
      child: Column(
        children: [
          Text(text),
          ListView(
            shrinkWrap: true,
            padding: const EdgeInsets.only(top: 20.0, left: 10.0),
            children: _createListEntry(users),
          ),
        ],
      )
    );
  }

  List<Container> _createListEntry(List<UserInfo> users) {
    List<Container> listRows = List<Container>();
    users.forEach((userInfo) {
      listRows.add(
        Container(
          padding: const EdgeInsets.only(top: 10.0),
          child: Text(userInfo.realName,
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      );
    });
    return listRows;
  }
}
