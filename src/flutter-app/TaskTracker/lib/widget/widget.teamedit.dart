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
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/widget/widget.teammembers.dart';
import 'package:flutter/material.dart';


class WidgetTeamEdit extends StatefulWidget {
  WidgetTeamEdit({Key key, this.title, this.teamId}) : super(key: key);

  final String title;
  final int    teamId;

  @override
  _WidgetTeamEditState createState() => _WidgetTeamEditState(teamId: teamId);
}

class _WidgetTeamEditState extends State<WidgetTeamEdit> {

  int teamId;

  bool  _newTeam;
  Team  _currentTeam;
  bool  _active = false;
  final _serviceTeam = ServiceTeam();
  final _textEditingControllerName = TextEditingController();
  final _textEditingControllerDescription = TextEditingController();
  final _widgetTeamMembers = WidgetTeamMembers();

  _WidgetTeamEditState({this.teamId = 0}) {
    _newTeam = teamId == 0;
  }

  @override
  void initState() {
    super.initState();
    if (!_newTeam) {
      _retrieveTeam();
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
        constraints: BoxConstraints(maxWidth: Config.defaultEditorWidth),
        child: Column(
          children: [
            ListView(
              shrinkWrap: true,
              children: <Widget>[
                Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(top: 20.0),
                      child: Text(
                        'Edit Team Settings',
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                    Visibility(
                      visible: _newTeam == false,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 10.0),
                        child: Text(
                          'Name: ' + _textEditingControllerName.text,
                        ),
                      ),
                    ),
                    Wrap(
                      spacing: 5,
                      runSpacing: 10,
                      children: [
                        Form(
                          child: Container(
                            width: 350,
                            padding: const EdgeInsets.all(10.0),
                            child: Column(
                              children: [
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerName,
                                    decoration: InputDecoration(
                                      labelText: 'Name',
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerDescription,
                                    maxLines: 3,
                                    maxLength: 255,
                                    decoration: InputDecoration(
                                      labelText: 'Description',
                                    ),
                                  ),
                                ),
                                Visibility(
                                  visible: !_newTeam,
                                  child: Row(
                                    children: [
                                      Padding(
                                          padding: EdgeInsets.only(left: 10, right: 20.0),
                                          child:
                                          Text('Active',
                                            textAlign: TextAlign.left,
                                          )
                                      ),
                                      Padding(
                                        padding: EdgeInsets.only(top: 0.0),
                                        child:
                                        Checkbox(
                                          value: _active,
                                          onChanged: (value) {
                                            setState(() {
                                              _active = value;
                                            });
                                          },
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                        LayoutBuilder(
                            builder: (BuildContext context, BoxConstraints constraints) {
                              double w = constraints.maxWidth < 535 ? 350 : 200;
                              return ConstrainedBox(
                                constraints: BoxConstraints(maxWidth: w),
                                child: Padding(
                                  padding: EdgeInsets.only(
                                      top: _newTeam ? 40.0 : 20.0, right: 10, left: 10
                                  ),
                                  child: _widgetTeamMembers,
                                ),
                              );
                            }
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: EdgeInsets.only(top: 15.0, right: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text('Cancel'),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, left: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newTeam ? ButtonID.CREATE : ButtonID.APPLY),
                    onPressed: () {
                      if (_newTeam) {
                        _createTeam(context);
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

  void _createTeam(BuildContext context) {
    if (_textEditingControllerName.text.isEmpty) {
      DialogModal(context).show("Attention", "Choose a team name!", true);
      return;
    }

    Team team = new Team();
    team.name = _textEditingControllerName.text;
    team.description = _textEditingControllerDescription.text;
    team.users = _widgetTeamMembers.getMemberIDs();

    _serviceTeam
        .createTeam(team)
        .then((id) {
          DialogModal(context).show("New Team", "New team was successfully created.", false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = "Could not create new team!\nReason: A team with given name already exists.";
          }
          else {
            text = "Could not create new team!\nReason:" + err.toString();
          }
          DialogModal(context).show("Attention", text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    Team team = new Team();
    team.id = _currentTeam.id;
    team.name = _textEditingControllerName.text;
    team.description = _textEditingControllerDescription.text;
    team.active = _active;
    team.users = _widgetTeamMembers.getMemberIDs();

    _serviceTeam
      .editTeam(team)
      .then((success) {
          if (success) {
            DialogModal(context).show("Edit Team", "All changes successfully applied.", false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not apply changes! Reason:" + err.toString(), true);
        }
      );
  }

  void _retrieveTeam() {
    if(teamId == 0) {
      print('Internal error, use this widget for an authenticated user');
      return;
    }

    _serviceTeam
        .getTeam(teamId)
        .then((team) {
          _currentTeam = team;
          _textEditingControllerName.text = _currentTeam.name;
          _textEditingControllerDescription.text = _currentTeam.description;
          _active = _currentTeam.active;

          _widgetTeamMembers.setMemberIDs(_currentTeam.users);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not retrieve team! Reason: " + err.toString(), true);
        }
    );
  }
}
