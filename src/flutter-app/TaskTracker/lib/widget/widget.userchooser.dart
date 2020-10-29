/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.chooseusers.dart';
import 'package:TaskTracker/dialog/dialogtwobuttons.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetUserChooser extends StatefulWidget {
  WidgetUserChooser({Key key, this.title = 'Users'}) : super(key: key);

  final String title;
  final _WidgetUserChooserState _widgetUserChooserState = _WidgetUserChooserState();

  @override
  _WidgetUserChooserState createState() => _widgetUserChooserState;

  void setUsers(List<UserInfo> users) {
    _widgetUserChooserState.setUsers(users);
  }

  List<UserInfo> getUsers() {
    return _widgetUserChooserState.getUsers();
  }

  void setUserIDs(List<int> users) {
    _widgetUserChooserState.setUserIDs(users);
  }

  List<int> getUserIDs() {
    return _widgetUserChooserState.getUsers()
        .map((user) => user.id).toList();
  }

  void setReadOnly(bool readOnly) {
    _widgetUserChooserState.setReadOnly(readOnly);
  }
}

class _WidgetUserChooserState extends State<WidgetUserChooser> {

  final _serviceUser = ServiceUser();

  List<Widget >   _usersWidget = List<Widget>();
  List<UserInfo>  _users = List<UserInfo>();
  bool            _readOnly = false;

  void setUsers(List<UserInfo> users) {
    _users = users;
  }

  List<UserInfo> getUsers() {
    return _users;
  }

  void setUserIDs(List<int> userIDs) {
    _users.clear();
    userIDs.forEach((userID) {
      _serviceUser.getUser(userID).then((userInfo) {
        _users.add(userInfo);
        _createUI();
      });
    });
  }

  void setReadOnly(bool readOnly) {
    this._readOnly = readOnly;
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 1,
      child: (
          Padding(
            padding: const EdgeInsets.all(10.0),
            child:
            Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.only(left: 0.0, top: 12.0),
                      child: Text(
                        widget.title,
                        style: TextStyle(fontWeight: FontWeight.w600),
                        textAlign: TextAlign.left,
                      ),
                    ),
                    Spacer(),
                    Visibility(
                      visible: !_readOnly,
                      child:
                      Padding(
                        padding: const EdgeInsets.all(0.0),
                        child:
                        CircleButton.create(20, Icons.add, () {
                          DialogChooseUsers(context).show(Translator.text('Common','Users'), Translator.text('WidgetUser','Add New User'))
                              .then((chosenUsers) {
                              if (chosenUsers != null && chosenUsers.length > 0) {
                                chosenUsers.forEach((userInfo) {
                                  bool userIsInList = false;
                                  _users.forEach((user) {
                                    if (user.id == userInfo.id) {
                                      userIsInList = true;
                                    }
                                  });
                                  if (!userIsInList) {
                                    _users.add(userInfo);
                                  }
                                  _createUI();
                                });
                              }
                            });
                          }
                        ),
                      ),
                    ),
                  ],
                ),
                Padding(
                  padding: const EdgeInsets.only(top: 10.0, left: 0.0, right: 0.0),
                  child:Container(
                    height: 180,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(5)),
                      border: Border.all(color: Config.LIST_BORDER_COLOR,),
                      color: Config.LIST_BACKGROUND_COLOR,
                    ),
                    child: ListView(
                      children: <Widget>[
                        Column(
                          children: _usersWidget),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          )
      ),
    );
  }

  void _createUI() {
    _usersWidget = List<Widget>();
    _users.forEach((userInfo) {
      _usersWidget.add(Padding(
            padding: EdgeInsets.all(5.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(userInfo.realName),
                CircleButton.create(16, Icons.delete, () {
                  DialogTwoButtonsModal(context)
                          .show(Translator.text('Common','Attention'),
                                Translator.text('WidgetUser','Do you really want to delete user ') + '\'' + userInfo.realName + '\'?',
                                ButtonID.YES, ButtonID.NO)
                          .then((button) {
                                if (button == ButtonID.YES) {
                                  _users.remove(userInfo);
                                  _createUI();
                              }
                          });
                }),
              ]
            ),
          ),
      );
    });
    setState(() {});
  }
}
