/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

/*
 Dialog for choosing a user.

 Example for usage:

  DialogChooseUser(context)
    .show('Choose User', 'Choose a user to add as member to team.')
    .then((chosenUsers) => chosenUsers.forEach((user) => print("CHOSEN USER: " + user.realName)));
 */
class DialogChooseUser {

  BuildContext _context;
  final _serviceUser = ServiceUser();
  final _chosenUsers = List<UserInfo>();
  dynamic _setState;
  List<Container> _listCandidates = List<Container>();
  List<Container> _listChosenUsers = List<Container>();

  DialogChooseUser(this._context);

  Future<List<UserInfo>> show(String title, String text) async {
    return showDialog(
        context: _context,
        builder: (context) {
          return StatefulBuilder(
            builder: (context, setState) {
              return AlertDialog(
                title: Text(title),
                //scrollable: true,
                content: _createUsersUI(text, setState),
                actions: <Widget>[
                  FlatButton(
                    child: Text(ButtonID.CANCEL),
                    onPressed: () {
                      Navigator.of(_context).pop(List<UserInfo>());
                    },
                  ),
                  FlatButton(
                    child: Text(ButtonID.CHOOSE),
                    onPressed: () {
                      Navigator.of(_context).pop(_chosenUsers);
                    },
                  ),
                ],
              );
            },
          );
        },
      );
  }

  SizedBox _createUsersUI(String text, setState) {
    _setState = setState;
    return SizedBox(
      width: 400,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(text, textAlign: TextAlign.left),
          Padding(
            padding: EdgeInsets.only(top: 25.0, bottom: 10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Find User',
                  style: TextStyle(fontWeight: FontWeight.w500)
                ),
                TextField(
                  decoration: InputDecoration(
                      hintText: 'Enter at least 3 characters'
                  ),
                  onChanged: (value) {
                      _searchUser(value).then((value) {
                        _updateUI();
                    });
                  },
                ),
              ],
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: ListView(
                  shrinkWrap: true,
                  padding: const EdgeInsets.only(top:10.0, left: 0.0),
                  children: [
                    Text('Found Users'),
                    ... _listCandidates,
                  ]
                ),
              ),
              Expanded(
                flex: 1,
                child: ListView(
                  shrinkWrap: true,
                  padding: const EdgeInsets.only(top: 10.0, left: 10.0),
                  children: [
                    Text('Chosen Users'),
                    ... _listChosenUsers,
                  ],
                ),
              ),
            ],
          ),
        ],
      )
    );
  }

  void _createCandidates(List<UserInfo> users) {
    _listCandidates.clear();
    users.forEach((userInfo) {
      _listCandidates.add(
        Container(
          padding: const EdgeInsets.only(top: 5.0),
          child:
            ListTile(
              title: Text(userInfo.realName, style: TextStyle(fontWeight: FontWeight.w600)),
              trailing: CircleButton.create(24, Icons.arrow_right, 16, () {
                if (!_chosenUsers.contains(userInfo)) {
                  _chosenUsers.add(userInfo);
                  _updateChosenUsersUI();
                }
              }),
          ),
        ),
      );
    });
  }

  void _updateChosenUsersUI() {
    _listChosenUsers.clear();
    _chosenUsers.forEach((userInfo) {
      _listChosenUsers.add(
        Container(
          padding: const EdgeInsets.only(top: 5.0),
          child:
          ListTile(
              title: Text(userInfo.realName, style: TextStyle(fontWeight: FontWeight.w600)),
              trailing: CircleButton.create(24, Icons.remove, 16, () {
                _chosenUsers.remove(userInfo);
                _updateChosenUsersUI();
              }),
          ),
        ),
      );
    });
    _updateUI();
  }

  Future<void> _searchUser(String value) async {
    if (value.length > 2) {
      await _serviceUser.searchUser(value)
          .then((users) {
            _createCandidates(users);
            return Future<void>.value(users.length);
      });
    }
    else {
      _listCandidates = List<Container>();
    }
  }

  void _updateUI() {
    if (_setState != null) {
      _setState(() => {});
    }
  }
}
