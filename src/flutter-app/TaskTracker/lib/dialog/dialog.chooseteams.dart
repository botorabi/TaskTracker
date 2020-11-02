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
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

/*
 Dialog for choosing teams.

 Example for usage:

  DialogChooseTeams(context)
    .show('Choose Teams', 'Find and add new teams.')
    .then((chosenTeams) => your code here));
 */
class DialogChooseTeams {

  BuildContext _context;
  final _serviceTeam = ServiceTeam();
  final _chosenTeams = List<Team>();
  final _controller = ScrollController();
  dynamic _setState;
  List<Container> _listCandidates = List<Container>();
  List<Container> _listChosenTeams = List<Container>();

  DialogChooseTeams(this._context);

  Future<List<Team>> show(String title, String text) async {
    await _searchTeam('*');
    return showDialog(
      context: _context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: Text(title),
              content: _createTeamsUI(text, setState),
              actions: <Widget>[
                FlatButton(
                  child: Text(Translator.text('Common', ButtonID.CANCEL)),
                  onPressed: () {
                    Navigator.of(_context).pop(List<Team>());
                  },
                ),
                FlatButton(
                  child: Text(Translator.text('Common', ButtonID.CHOOSE)),
                  onPressed: () {
                    Navigator.of(_context).pop(_chosenTeams);
                  },
                ),
              ],
            );
          },
        );
      },
    );
  }

  SizedBox _createTeamsUI(String text, setState) {
    _setState = setState;
    return SizedBox(
      width: 400,
      height: 340,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(text, textAlign: TextAlign.left),
          Padding(
            padding: EdgeInsets.only(top: 25.0, bottom: 20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(Translator.text('WidgetTeam','Find Team'),
                    style: TextStyle(fontWeight: FontWeight.w500)
                ),
                TextField(
                  decoration: InputDecoration(
                      hintText: Translator.text('Common','Enter at least 3 characters')
                  ),
                  onChanged: (value) {
                    _searchTeam(value).then((value) {
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
              Card(
                elevation: 2,
                child:
                Padding(
                  padding: EdgeInsets.all(5.0),
                  child:
                  Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(
                        padding: EdgeInsets.all(5.0),
                        child: Text(Translator.text('WidgetTeam','Found Teams'),
                          style: TextStyle(
                              fontWeight: FontWeight.w500),
                          textAlign: TextAlign.left,
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.all(5.0),
                        child:
                        Container(
                          width: 170,
                          height: 154,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.all(Radius.circular(5)),
                            border: Border.all(color: Config.LIST_BORDER_COLOR),
                            color: Config.LIST_BACKGROUND_COLOR,
                          ),
                          child: ListView(
                            shrinkWrap: true,
                            children: <Widget> [... _listCandidates],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              Card(
                elevation: 2,
                child:
                Padding(
                  padding: EdgeInsets.all(5.0),
                  child:
                  Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(
                        padding: EdgeInsets.all(5.0),
                        child: Text(Translator.text('WidgetTeam','Chosen Teams'),
                          style: TextStyle(
                              fontWeight: FontWeight.w500),
                          textAlign: TextAlign.left,
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.all(5.0),
                        child:
                        Container(
                          width: 170,
                          height: 154,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.all(Radius.circular(5)),
                            border: Border.all(color: Config.LIST_BORDER_COLOR),
                            color: Config.LIST_BACKGROUND_COLOR,
                          ),
                          child: ListView(
                            controller: _controller,
                            shrinkWrap: true,
                            children: <Widget>[... _listChosenTeams],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _createTeamCandidates(List<Team> teams) {
    _listCandidates.clear();
    teams.forEach((team) {
      _listCandidates.add(
        Container(
          child:
          ListTile(
            dense: true,
            title: Text(team.name, style: TextStyle(fontSize: 14.0)),
            trailing: CircleButton.create(18, Icons.add_circle_rounded, () {
              if (_addNewChosenTeam(team)) {
                _updateChosenTeamsUI();
                _scrollToLastChosenTeam();
              }
            }),
          ),
        ),
      );
    });
  }

  void _updateChosenTeamsUI() {
    _listChosenTeams.clear();
    _chosenTeams.forEach((team) {
      _listChosenTeams.add(
        Container(
          child:
          ListTile(
            dense: true,
            title: Text(team.name, style: TextStyle(fontSize: 14.0)),
            trailing: CircleButton.create(18, Icons.remove_circle_outlined, () {
              _chosenTeams.remove(team);
              _updateChosenTeamsUI();
            }),
          ),
        ),
      );
    });
    _updateUI();
  }

  void _updateUI() {
    if (_setState != null) {
      _setState(() => {});
    }
  }

  bool _addNewChosenTeam(Team team) {
    bool teamIsInList = false;
    _chosenTeams.forEach((team) {
      if (team.id == team.id) {
        teamIsInList = true;
      }
    });
    if (!teamIsInList) {
      _chosenTeams.add(team);
    }
    return !teamIsInList;
  }

  void _scrollToLastChosenTeam() {
    _controller.animateTo(_controller.position.maxScrollExtent + 50,
        duration: Duration(seconds: 1),
        curve: Curves.fastOutSlowIn);
  }

  Future<void> _searchTeam(String value) async {
    String filter = (value != null && value.length > 0) ? value : '*';
    await _serviceTeam.searchTeam(filter)
        .then((teams) {
          _createTeamCandidates(teams);
          return Future<void>.value(teams.length);
    });
  }
}
