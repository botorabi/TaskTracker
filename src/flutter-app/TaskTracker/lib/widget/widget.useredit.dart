/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:io';

import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:TaskTracker/widget/widget.roles.dart';
import 'package:flutter/material.dart';


class WidgetUserEdit extends StatefulWidget {
  WidgetUserEdit({Key key, this.title, this.userId}) : super(key: key);

  final String title;
  final int    userId;

  @override
  _WidgetUserEditState createState() => _WidgetUserEditState(userId: userId);
}

class _WidgetUserEditState extends State<WidgetUserEdit> {

  int userId;

  bool     _newUser;
  UserInfo _currentUserInfo;
  final _serviceUser = ServiceUser();
  final _textEditingControllerRealName = TextEditingController();
  final _textEditingControllerLoginName = TextEditingController();
  final _textEditingControllerEMail = TextEditingController();
  final _textEditingControllerPassword = TextEditingController();
  final _textEditingControllerPasswordRepeat = TextEditingController();

  WidgetRoles _widgetRoles = WidgetRoles(title: 'Roles');

  _WidgetUserEditState({this.userId}) {
    if (userId != 0) {
      _newUser = false;
    }
    else {
      _newUser = true;
    }
  }

  @override
  void initState() {
    super.initState();
    if (!_newUser) {
      _retrieveUserInfo();
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4.0,
      margin: const EdgeInsets.all(30.0),
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: Config.DEFAULT_EDITOR_WIDTH),
        child: Column(
          children: <Widget>[
            ListView(
              shrinkWrap: true,
              children: <Widget>[
                Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(top: 20.0),
                      child: Text(
                        'Edit User Settings',
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                    Visibility(
                      visible: _newUser == false,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 10.0),
                        child: Text(
                          'Login: ' + _textEditingControllerLoginName.text,
                        ),
                      ),
                    ),
                  ],
                ),
                Wrap(
                  spacing: 5,
                  runSpacing: 10,
                  children: [
                    Form(
                      child: Container(
                        width: 350,
                        padding: const EdgeInsets.all(20.0),
                        child: Column(
                          children: [
                            Visibility(
                              visible: _newUser == true,
                              child: Padding(
                                padding: EdgeInsets.all(10.0),
                                child: TextFormField(
                                  controller: _textEditingControllerLoginName,
                                  autofocus: true,
                                  decoration: InputDecoration(
                                    labelText: 'Login Name',
                                  ),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerRealName,
                                decoration: InputDecoration(
                                  labelText: 'Real Name',
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerEMail,
                                decoration: InputDecoration(
                                  labelText: 'E-Mail',
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerPassword,
                                obscureText: true,
                                decoration: InputDecoration(
                                  labelText: 'Password',
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerPasswordRepeat,
                                obscureText: true,
                                decoration: InputDecoration(
                                  labelText: 'Repeat Password',
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    LayoutBuilder(
                      builder: (BuildContext context, BoxConstraints constraints) {
                        double topPadding = constraints.maxWidth < 535 ? 0.0 : 50.0;
                        double w = constraints.maxWidth < 535 ? 350 : 220;
                        return ConstrainedBox(
                          constraints: BoxConstraints(maxWidth: w),
                          child:
                          Padding(
                            padding: EdgeInsets.only(left: 20, top: topPadding),
                            child: Config.authStatus.isAdmin() ? _widgetRoles : Text('Role: User'),
                          ),
                        );
                      }
                    ),
                  ],
                ),
              ],
            ),

            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: EdgeInsets.only(top: 10.0, right: 15.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text('Cancel'),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 10.0, right: 15.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newUser ? ButtonID.CREATE : ButtonID.APPLY),
                    onPressed: () {
                      if (_newUser) {
                        _createUser(context);
                      }
                      else {
                        _applyChanges(context);
                      }
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _createUser(BuildContext context) {
    if (_textEditingControllerPassword.text != _textEditingControllerPasswordRepeat.text) {
      DialogModal(context).show("Attention", "Passwords mismatch!", true);
      return;
    }
    if (_textEditingControllerPassword.text.isEmpty) {
      DialogModal(context).show("Attention", "Choose a password with at least 8 characters!", true);
      return;
    }
    if (_textEditingControllerEMail.text.isEmpty) {
      DialogModal(context).show("Attention", "Choose a valid E-Mail address!", true);
      return;
    }

    UserInfo userInfo = UserInfo();
    userInfo.login = _textEditingControllerLoginName.text;
    userInfo.realName = _textEditingControllerRealName.text;
    userInfo.email = _textEditingControllerEMail.text;
    userInfo.password = _textEditingControllerPassword.text;
    userInfo.roles = _widgetRoles.getUserRoles();

    _serviceUser
        .createUser(userInfo)
        .then((id) {
          DialogModal(context).show("New User", "New user was successfully created.", false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = "Could not create new user!\nReason: Given login name is already in use.";
          }
          else {
            text = "Could not create new user!\nReason:" + err.toString();
          }
          DialogModal(context).show("Attention", text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    if (_textEditingControllerPassword.text != _textEditingControllerPasswordRepeat.text) {
      DialogModal(context).show("Attention", "Passwords mismatch!", true);
      return;
    }
    if (_textEditingControllerEMail.text.isEmpty) {
      DialogModal(context).show("Attention", "Choose a valid E-Mail address!", true);
      return;
    }

    UserInfo userInfo = UserInfo();
    userInfo.id = _currentUserInfo.id;
    userInfo.login = _currentUserInfo.login;
    userInfo.email = _textEditingControllerEMail.text;
    userInfo.realName = _textEditingControllerRealName.text;
    if (_textEditingControllerPassword.text.isNotEmpty) {
      userInfo.password = _textEditingControllerPassword.text;
    }
    userInfo.roles = _widgetRoles.getUserRoles();

    _serviceUser
      .editUser(userInfo)
      .then((success) {
          if (success) {
            DialogModal(context).show("Edit User", "All changes successfully applied.", false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not apply changes! Reason:" + err.toString(), true);
        }
      );
  }

  void _retrieveUserInfo() {
    if(userId == 0) {
      print('Internal error, use this widget for an authenticated user');
      return;
    }

    _serviceUser
        .getUser(userId)
        .then((userInfo) {
          _currentUserInfo = userInfo;
          _textEditingControllerRealName.text = _currentUserInfo.realName;
          _textEditingControllerLoginName.text = _currentUserInfo.login;
          _textEditingControllerEMail.text = _currentUserInfo.email;
          _textEditingControllerPassword.text = '';
          _textEditingControllerPasswordRepeat.text = '';
          _widgetRoles.setUserRoles(_currentUserInfo.roles);
          _widgetRoles.setReadOnly(!(Config.authStatus.isAdmin() && (userId != Config.authStatus.userId)));
          if (_currentUserInfo.isAdmin()) {
            _widgetRoles.setReadOnly(true);
          }

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not retrieve user info! Reason: " + err.toString(), true);
        }
    );
  }
}
