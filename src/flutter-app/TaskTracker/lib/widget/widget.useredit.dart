/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:io';

import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
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
  final _textEditingControllerPassword = TextEditingController();
  final _textEditingControllerPasswordRepeat = TextEditingController();
  final _focusLoginName = FocusNode();

  _WidgetUserEditState({this.userId}) {
    if (userId != 0) {
      _newUser = false;
      retrieveUserInfo();
    }
    else {
      _newUser = true;
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
      child: SizedBox(
        width: 350,
        height: _newUser == false ? 420 : 450,
        child: Column(
          children: <Widget>[
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
            Form(
              child: Container(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    Visibility(
                      visible: _newUser == true,
                      child: Padding(
                        padding: EdgeInsets.all(10.0),
                        child: TextFormField(
                          controller: _textEditingControllerLoginName,
                          focusNode: _focusLoginName,
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
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Padding(
                          padding: EdgeInsets.only(top: 10.0, right: 10.0),
                          child: RaisedButton(
                            child: Text('Cancel'),
                            onPressed: () => { Navigator.of(context).pop('Cancel') },
                          ),
                        ),
                        Padding(
                          padding: EdgeInsets.only(top: 10.0, right: 10.0),
                          child: RaisedButton(
                            child: Text(_newUser ? 'Create' : 'Apply'),
                            onPressed: () {
                              if (_newUser) {
                                createUser(context);
                              }
                              else {
                                applyChanges(context);
                              }
                            },
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void createUser(BuildContext context) {
    if (_textEditingControllerPassword.text != _textEditingControllerPasswordRepeat.text) {
      DialogModal(context).show("Attention", "Passwords mismatch!", true);
      return;
    }
    if (_textEditingControllerPassword.text.isEmpty) {
      DialogModal(context).show("Attention", "Choose a password with at least 8 characters!", true);
      return;
    }

    UserInfo userInfo = UserInfo();
    userInfo.login = _textEditingControllerLoginName.text;
    userInfo.realName = _textEditingControllerRealName.text;
    userInfo.password = _textEditingControllerPassword.text;

    _serviceUser
        .create(userInfo)
        .then((id) {
          DialogModal(context).show("New User", "New user was successfully created.", false);
          _textEditingControllerLoginName.text = '';
          _textEditingControllerRealName.text = '';
          _textEditingControllerPassword.text = '';
          _textEditingControllerPasswordRepeat.text = '';

          _focusLoginName.requestFocus();
          setState(() {});
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

  void applyChanges(BuildContext context) {
    if (_textEditingControllerPassword.text != _textEditingControllerPasswordRepeat.text) {
      DialogModal(context).show("Attention", "Passwords mismatch!", true);
      return;
    }

    UserInfo userInfo = UserInfo();
    userInfo.id = _currentUserInfo.id;
    userInfo.login = _currentUserInfo.login;
    userInfo.realName = _textEditingControllerRealName.text;
    if (_textEditingControllerPassword.text.isNotEmpty) {
      userInfo.password = _textEditingControllerPassword.text;
    }

    _serviceUser
      .edit(userInfo)
      .then((success) {
          if (success) {
            DialogModal(context).show("User Profile", "All changes successfully applied.", false);
          }
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not apply changes! Reason:" + err.toString(), true);
        }
      );
  }

  void retrieveUserInfo() {
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
          _textEditingControllerPassword.text = '';
          _textEditingControllerPasswordRepeat.text = '';
          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not retrieve user info! Reason: " + err.toString(), true);
        }
    );
  }
}
