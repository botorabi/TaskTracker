/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetRoles extends StatefulWidget {
  WidgetRoles({Key key, this.title}) : super(key: key);

  final String title;
  final _WidgetRolesState _widgetRoleState = _WidgetRolesState();

  @override
  _WidgetRolesState createState() => _widgetRoleState;

  void setUserRoles(List<String> roles) {
    _widgetRoleState.setUserRoles(roles);
  }

  List<String> getUserRoles() {
    return _widgetRoleState.getUserRoles();
  }

  void setReadOnly(bool readOnly) {
    _widgetRoleState.setReadOnly(readOnly);
  }
}

class _WidgetRolesState extends State<WidgetRoles> {

  bool _stateReady = false;
  final _serviceUser = ServiceUser();
  List<Container> _rolesWidget = List<Container>();
  List<String> _userRoles = List<String>();
  List<String> _availableRoles;
  bool _readOnly = false;

  @override
  void initState() {
    super.initState();
    _retrieveRoles();
  }

  void setUserRoles(List<String> roles) {
    _userRoles = roles;
    _createRolesUI(_availableRoles);
  }

  List<String> getUserRoles() {
    return _userRoles;
  }

  void setReadOnly(bool readOnly) {
    this._readOnly = readOnly;
    _createRolesUI(_availableRoles);
  }

  @override
  void dispose() {
    _stateReady = false;
    super.dispose();
  }

  void _updateState() {
    if (_stateReady) {
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    _stateReady = true;
    return Container(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.all(10.0),
            child: Text(
              widget.title,
              textAlign: TextAlign.left,
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: _rolesWidget,
          ),
        ],
      ),
    );
  }

  void _retrieveRoles() {
    _serviceUser
        .getAvailableRoles()
        .then((roles) {
          _availableRoles = roles;
          _createRolesUI(roles);
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('WidgetRoles', 'Could not retrieve user Roles! Reason: ') + err.toString(), true);
        }
    );
  }

  void _createRolesUI(List<String> availableRoles) {
    if (availableRoles == null) {
      return;
    }
    _rolesWidget = List<Container>();
    availableRoles.forEach((element) {
      _rolesWidget.add(Container(
        child:
          Row(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Checkbox(
                value: (_userRoles != null) ? _userRoles.contains(element) : false,
                onChanged: _readOnly ? null :
                    (value) {
                      _userRoles.remove(element);
                      if (value) {
                        _userRoles.add(element);
                    }
                    _createRolesUI(_availableRoles);
                  },
              ),
              Text(element.replaceAll(UserInfo.ROLE_PREFIX,'')),
            ],
          )
        )
      );
    });

    _updateState();
  }
}
