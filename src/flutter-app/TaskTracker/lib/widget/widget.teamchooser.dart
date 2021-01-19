/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.chooseteams.dart';
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetTeamChooser extends StatefulWidget {
  WidgetTeamChooser({Key key, this.title = 'Teams'}) : super(key: key);

  final String title;
  final _WidgetTeamChooserState _widgetTeamChooserState = _WidgetTeamChooserState();

  @override
  _WidgetTeamChooserState createState() => _widgetTeamChooserState;

  void setTeams(List<Team> teams) {
    _widgetTeamChooserState.setTeams(teams);
  }

  List<Team> getTeams() {
    return _widgetTeamChooserState.getTeams();
  }

  void setTeamIDs(List<int> teamIDs) {
    _widgetTeamChooserState.setTeamIDs(teamIDs);
  }

  List<int> getTeamIDs() {
    return _widgetTeamChooserState.getTeams()
        .map((team) => team.id).toList();
  }

  void setReadOnly(bool readOnly) {
    _widgetTeamChooserState.setReadOnly(readOnly);
  }
}

class _WidgetTeamChooserState extends State<WidgetTeamChooser> {

  final _serviceTeam = ServiceTeam();

  List<Widget> _teamsWidget = List<Widget>();
  List<Team>   _teams = List<Team>();
  bool         _readOnly = false;

  void setTeams(List<Team> teams) {
    _teams = teams;
  }

  List<Team> getTeams() {
    return _teams;
  }

  void setTeamIDs(List<int> teamIDs) {
    _teams.clear();
    teamIDs.forEach((teamID) {
      _serviceTeam.getTeam(teamID).then((team) {
        _teams.add(team);
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
                        CircleButton.create(18, Icons.add_circle_rounded, () {
                          DialogChooseTeams(context).show(Translator.text('Common', 'Teams'), Translator.text('WidgetTeam', 'Add New Team'))
                              .then((chosenTeams) {
                              if (chosenTeams != null && chosenTeams.length > 0) {
                                chosenTeams.forEach((chosenTeam) {
                                  bool teamIsInList = false;
                                  _teams.forEach((team) {
                                    if (team.id == chosenTeam.id) {
                                      teamIsInList = true;
                                    }
                                  });
                                  if (!teamIsInList) {
                                    _teams.add(chosenTeam);
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
                        Column(children: _teamsWidget),
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
    _teamsWidget = List<Widget>();
    _teams.forEach((team) {
      _teamsWidget.add(Padding(
          padding: EdgeInsets.all(5.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(team.name),
              CircleButton.create(18, Icons.remove_circle_outlined, () {
                _teams.remove(team);
                _createUI();
              }),
            ]
          ),
        ),
      );
    });
    setState(() {});
  }
}
