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
import 'package:TaskTracker/translator.dart';
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
        child: Column(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(top: 20.0),
              child: Text(
                Translator.text('WidgetLogin', 'Login User'),
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
                          labelText: Translator.text('WidgetLogin', 'Login Name'),
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
                        onFieldSubmitted: (value) {
                          loginUser(context);
                        },
                        onEditingComplete: () {
                          _focusButton.requestFocus();
                        },
                        decoration: InputDecoration(
                          labelText: Translator.text('WidgetLogin', 'Password'),
                        ),
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 20.0),
                      child: RaisedButton(
                        child: Text(Translator.text('WidgetLogin', 'Login'),),
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
        DialogModal(context).show(
            Translator.text('Common', 'Attention'),
            Translator.text('WidgetLogin', 'Could not login. Please, check your credentials!'), true);
      }
    },
    onError: (err) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetLogin', 'Could not login! Reason: ') + err.toString(), true);
    });
  }
}
