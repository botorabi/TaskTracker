/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/service.login.dart';
import 'package:flutter/material.dart';


class WidgetLogin extends StatefulWidget {
  WidgetLogin({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _WidgetLoginState createState() => _WidgetLoginState();
}

class _WidgetLoginState extends State<WidgetLogin> {

  final _serviceLogin = ServiceLogin();
  final _textEditingControllerLoginName = TextEditingController();
  final _textEditingControllerPassword = TextEditingController();
  final _focusButton = FocusNode();
  final _focusPassword = FocusNode();

  @override
  void dispose() {
    _textEditingControllerLoginName.dispose();
    _textEditingControllerPassword.dispose();
    _focusButton.dispose();
    _focusPassword.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4.0,
      margin: const EdgeInsets.all(30.0),
      child: SizedBox(
        width: 350,
        height: 305,
        child: Column(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(top: 20.0),
              child: Text(
                'Login User',
                style: Theme.of(context).textTheme.headline6,
              ),
            ),
            Form(
              child: Container(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    Padding(
                      padding: EdgeInsets.all(10.0),
                      child: TextFormField(
                        controller: _textEditingControllerLoginName,
                        decoration: InputDecoration(
                          labelText: 'Login Name',
                        ),
                        onEditingComplete: () {
                          _focusPassword.requestFocus();
                        },
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.all(10.0),
                      child: TextFormField(
                        controller: _textEditingControllerPassword,
                        focusNode: _focusPassword,
                        obscureText: true,
                        onEditingComplete: () {
                          _focusButton.requestFocus();
                        },
                        decoration: InputDecoration(
                          labelText: 'Password',
                        ),
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 15.0),
                      child: RaisedButton(
                        child: Text('Login'),
                          focusNode: _focusButton,
                          onPressed: () {
                            loginUser(context);
                        },
                      ),
                    ),
                  ]
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void showModalDialog(String title, String text, bool warn) {
    DialogModal(context).show(title, text, warn);
  }

  void loginUser(BuildContext context) {
    if (_textEditingControllerLoginName.text.isEmpty || _textEditingControllerPassword.text.isEmpty) {
      return;
    }
    this._serviceLogin.loginUser(
        _textEditingControllerLoginName.text,
        _textEditingControllerPassword.text).then((authStatus) {
      Config.authStatus = authStatus;
      if (Config.authStatus.authenticated == true) {
        Navigator.pushNamed(context, NavigationLinks.NAV_HOME);
      }
      else {
        showModalDialog("Attention", "Could not login. Check your credentials!", true);
      }
    },
    onError: (err) {
      showModalDialog("Attention", "Could not login! Reason: " + err.toString(), true);
    });
  }
}
