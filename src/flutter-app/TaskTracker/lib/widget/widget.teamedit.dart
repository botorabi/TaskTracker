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
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.userchooser.dart';
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
  final _widgetTeamMembers = WidgetUserChooser(title: Translator.text('WidgetTeam', 'Team Members'));
  final _widgetTeamLeaders = WidgetUserChooser(title: Translator.text('WidgetTeam', 'Team Leaders'));

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
        constraints: BoxConstraints(maxWidth: Config.DEFAULT_EDITOR_WIDTH),
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
                        Translator.text('WidgetTeam', 'Edit Team Settings'),
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                    Visibility(
                      visible: _newTeam == false,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 10.0),
                        child: Text(
                          Translator.text('Common', 'Name') + ': ' + _textEditingControllerName.text,
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
                                      labelText: Translator.text('Common', 'Name'),
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerDescription,
                                    maxLines: 5,
                                    maxLength: 255,
                                    decoration: InputDecoration(
                                      labelText: Translator.text('Common', 'Description'),
                                      border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(0.0))),
                                    ),
                                  ),
                                ),
                                Visibility(
                                  //! NOTE currently we do not use the active flag
                                  visible: false, //!_newTeam,
                                  child: Row(
                                    children: [
                                      Padding(
                                          padding: EdgeInsets.only(left: 10, right: 10.0),
                                          child:
                                          Text(Translator.text('Common', 'Active'),
                                            textAlign: TextAlign.left,
                                          )
                                      ),
                                      Spacer(),
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
                              double w = constraints.maxWidth < 535 ? 350 : 220;
                              return ConstrainedBox(
                                constraints: BoxConstraints(maxWidth: w),
                                child:
                                  Column(
                                    children: [
                                      Padding(
                                        padding: EdgeInsets.only(top: _newTeam ? 40.0 : 20.0, right: 10, left: 5),
                                        child: _widgetTeamMembers,
                                      ),
                                      Padding(
                                        padding: EdgeInsets.only(top: 20.0, right: 10, left: 5),
                                        child: _widgetTeamLeaders,
                                      ),
                                    ],
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
                    child: Text(Translator.text('Common', 'Cancel')),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, left: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newTeam ? Translator.text('Common', ButtonID.CREATE) : Translator.text('Common', ButtonID.APPLY)),
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
    if (!_validateInput()) {
      return;
    }

    Team team = new Team();
    team.name = _textEditingControllerName.text;
    team.description = _textEditingControllerDescription.text;
    team.userIDs = _widgetTeamMembers.getUserIDs();
    team.teamLeaderIDs = _widgetTeamLeaders.getUserIDs();

    _serviceTeam
        .createTeam(team)
        .then((id) {
          DialogModal(context).show(Translator.text('WidgetTeam', 'New Team'), Translator.text('WidgetTeam', 'New team was successfully created.'), false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = Translator.text('WidgetTeam', 'Could not create new team!\nReason: A team with given name already exists.');
          }
          else {
            text = Translator.text('WidgetTeam', 'Could not create new team!\nReason: ') + err.toString();
          }
          DialogModal(context).show(Translator.text('Common', 'Attention'), text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    if (!_validateInput()) {
      return;
    }

    Team team = new Team();
    team.id = _currentTeam.id;
    team.name = _textEditingControllerName.text;
    team.description = _textEditingControllerDescription.text;
    team.active = _active;
    team.userIDs = _widgetTeamMembers.getUserIDs();
    team.teamLeaderIDs = _widgetTeamLeaders.getUserIDs();

    _serviceTeam
      .editTeam(team)
      .then((success) {
          if (success) {
            DialogModal(context).show(Translator.text('WidgetTeam', 'Edit Team'), Translator.text('Common', 'All changes successfully applied.'), false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show(Translator.text('Common', 'Attention'),
              Translator.text('Common', 'Could not apply changes! Reason: ') + err.toString(), true);
        }
      );
  }

  bool _validateInput() {
    if (_textEditingControllerName.text.isEmpty) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetTeam', 'Please, choose a team name!'), true);
      return false;
    }

    if (Config.authStatus.isTeamLead()) {
      List<int> userIDs = _widgetTeamLeaders.getUserIDs();
      if (!userIDs.contains(Config.authStatus.userId)) {
        DialogModal(context).show(Translator.text('Common', 'Attention'),
            Translator.text('WidgetTeam', 'You must be part of team leaders!'), true);
        return false;
      }
    }
    else {
      if (_widgetTeamLeaders.getUserIDs().length == 0) {
        DialogModal(context).show(Translator.text('Common', 'Attention'),
            Translator.text('WidgetTeam', 'You must define at least one team leader!'), true);
        return false;
      }
    }

    return true;
  }

  void _retrieveTeam() {
    if(teamId == 0) {
      print(Translator.text('Common', 'Internal error, use this widget for an authenticated user'));
      return;
    }

    _serviceTeam
        .getTeam(teamId)
        .then((team) {
          _currentTeam = team;
          _textEditingControllerName.text = _currentTeam.name;
          _textEditingControllerDescription.text = _currentTeam.description;
          _active = _currentTeam.active;

          _widgetTeamMembers.setUserIDs(_currentTeam.userIDs);
          _widgetTeamLeaders.setUserIDs(_currentTeam.teamLeaderIDs);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetTeam', 'Could not retrieve team! Reason: ') + err.toString(), true);
        }
    );
  }
}
