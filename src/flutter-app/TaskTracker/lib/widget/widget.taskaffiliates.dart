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
import 'package:TaskTracker/dialog/dialog.chooseusers.dart';
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';


class WidgetTaskAffiliates extends StatefulWidget {
  WidgetTaskAffiliates({Key key, this.title = 'Task Affiliates'}) : super(key: key);

  final String title;
  final _WidgetTaskAffiliatesState _widgetTaskAffiliatesState = _WidgetTaskAffiliatesState();

  @override
  _WidgetTaskAffiliatesState createState() => _widgetTaskAffiliatesState;

  void setUsers(List<UserInfo> users) {
    _widgetTaskAffiliatesState.setUsers(users);
  }

  List<UserInfo> getUsers() {
    return _widgetTaskAffiliatesState.getUsers();
  }

  void setUserIDs(List<int> userIDs) {
    _widgetTaskAffiliatesState.setUserIDs(userIDs);
  }

  List<int> getUserIDs() {
    return _widgetTaskAffiliatesState.getUsers()
        .map((user) => user.id).toList();
  }

  List<Team> getTeams() {
    return _widgetTaskAffiliatesState.getTeams();
  }

  void setTeamIDs(List<int> teamIDs) {
    _widgetTaskAffiliatesState.setTeamIDs(teamIDs);
  }

  List<int> getTeamIDs() {
    return _widgetTaskAffiliatesState.getTeams()
        .map((team) => team.id).toList();
  }

  void setReadOnly(bool readOnly) {
    _widgetTaskAffiliatesState.setReadOnly(readOnly);
  }
}

class _WidgetTaskAffiliatesState extends State<WidgetTaskAffiliates> {

  final _serviceUser = ServiceUser();
  final _serviceTeam = ServiceTeam();

  List<Container> _usersWidget = List<Container>();
  List<UserInfo>  _users = List<UserInfo>();
  List<Container> _teamsWidget = List<Container>();
  List<Team>      _teams = List<Team>();
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
        _createUsersUI();
      });
    });
  }

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
        _createTeamsUI();
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
                ////////////  ADD TEAMS
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.only(top: 0.0, left: 0.0),
                      child: Text(
                        "Affiliate Teams",
                        style: TextStyle(fontWeight: FontWeight.w600),
                        textAlign: TextAlign.left,
                      ),
                    ),
                    Spacer(),
                    Visibility(
                      visible: !_readOnly,
                      child:
                      Padding(
                        padding: const EdgeInsets.only(top: 0.0),
                        child:
                        CircleButton.create(20, Icons.add, () {
                          DialogChooseTeams(context).show('Teams', 'Add teams.')
                              .then((chosenTeams) {
                                if (chosenTeams != null && chosenTeams.length > 0) {
                                  chosenTeams.forEach((team) {
                                    bool teamIsInList = false;
                                    _teams.forEach((existingTeam) {
                                      if (existingTeam.id == team.id) {
                                        teamIsInList = true;
                                      }
                                    });
                                    if (!teamIsInList) {
                                      _teams.add(team);
                                    }
                                    _createTeamsUI();
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
                    height: 90,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(5)),
                      border: Border.all(color: Config.listBorderColor,),
                      color: Config.listBackgroundColor,
                    ),
                    child: ListView(
                      children: <Widget>[
                        Column(children: _teamsWidget),
                      ],
                    ),
                  ),
                ),

                ////////////  ADD USERS
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.only(top: 15.0, left: 0.0),
                      child: Text(
                        "Affiliate Users",
                        style: TextStyle(fontWeight: FontWeight.w600),
                        textAlign: TextAlign.left,
                      ),
                    ),
                    Spacer(),
                    Visibility(
                      visible: !_readOnly,
                      child:
                      Padding(
                        padding: const EdgeInsets.only(top: 15.0),
                        child:
                        CircleButton.create(20, Icons.add, () {
                          DialogChooseUsers(context).show('Users', 'Add users.')
                              .then((chosenUsers) {
                                if (chosenUsers != null && chosenUsers.length > 0) {
                                  chosenUsers.forEach((userInfo) {
                                    bool memberIsInList = false;
                                    _users.forEach((member) {
                                      if (member.id == userInfo.id) {
                                        memberIsInList = true;
                                      }
                                    });
                                    if (!memberIsInList) {
                                      _users.add(userInfo);
                                    }
                                    _createUsersUI();
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
                    height: 90,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(5)),
                      border: Border.all(color: Config.listBorderColor,),
                      color: Config.listBackgroundColor,
                    ),
                    child: ListView(
                      children: <Widget>[
                        Column(children: _usersWidget),
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

  void _createUsersUI() {
    _usersWidget = List<Container>();
    _users.forEach((userInfo) {
      _usersWidget.add(Container(
        child: Padding(
            padding: EdgeInsets.all(5.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(userInfo.realName),
                CircleButton.create(16, Icons.remove, () {
                  _users.remove(userInfo);
                  _createUsersUI();
                }),
              ]
            ),
          ),
        ),
      );
    });
    setState(() {});
  }

  void _createTeamsUI() {
    _teamsWidget = List<Container>();
    _teams.forEach((team) {
      _teamsWidget.add(Container(
        child: Padding(
          padding: EdgeInsets.all(5.0),
          child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(team.name),
                CircleButton.create(16, Icons.remove, () {
                  _teams.remove(team);
                  _createTeamsUI();
                }),
              ]
          ),
        ),
      ),
      );
    });
    setState(() {});
  }
}
