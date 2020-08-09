/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:html';

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/dialog/dialog.chooseuser.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';


class WidgetTeamMembers extends StatefulWidget {
  WidgetTeamMembers({Key key, this.title = 'Team Members'}) : super(key: key);

  final String title;
  final _WidgetTeamMembersState _widgetTeamMembersState = _WidgetTeamMembersState();

  @override
  _WidgetTeamMembersState createState() => _widgetTeamMembersState;

  void setMembers(List<UserInfo> members) {
    _widgetTeamMembersState.setMembers(members);
  }

  List<UserInfo> getMembers() {
    return _widgetTeamMembersState.getMembers();
  }

  void setMemberIDs(List<int> members) {
    _widgetTeamMembersState.setMemberIDs(members);
  }

  List<int> getMemberIDs() {
    /*
    List<int> ids = List<int>();
    _widgetTeamMembersState.getMembers().forEach((element) {
      ids.add(element.id);
    });
    return ids;
    */
    return _widgetTeamMembersState.getMembers()
        .map((member) => member.id) as List<int>;
  }

  void setReadOnly(bool readOnly) {
    _widgetTeamMembersState.setReadOnly(readOnly);
  }

  /// Call after using setMembers or setReadOnly in order to update the UI.
  void updateUI() {
    _widgetTeamMembersState.updateUI();
  }
}

class _WidgetTeamMembersState extends State<WidgetTeamMembers> {

  final _serviceUser = ServiceUser();

  List<Container> _membersWidget = List<Container>();
  List<UserInfo>  _members = List<UserInfo>();
  bool            _readOnly = false;

  void setMembers(List<UserInfo> members) {
    _members = members;
  }

  List<UserInfo> getMembers() {
    return _members;
  }

  void setMemberIDs(List<int> userIDs) {
    _members.clear();
    userIDs.forEach((userID) {
      _serviceUser.getUser(userID).then((userInfo) {
        _members.add(userInfo);
      });
    });
  }

  void setReadOnly(bool readOnly) {
    this._readOnly = readOnly;
  }

  void updateUI() {
    _createUI();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 0.0),
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
                  padding: const EdgeInsets.only(right: 10.0),
                  child:
                    CircleButton.create(24, Icons.add, 16, () {

                        /// TODO get all available users from back-end
                        List<UserInfo> availableUsers = List<UserInfo>();
                        for (int i = 0; i < 20; i++) {
                          UserInfo user = UserInfo();
                          user.realName = "Foo Bar" + i.toString();
                          availableUsers.add(user);
                        }
                        ////////////////////////////////////////////////

                        DialogChooseUser(context).show('Choose User', 'Choose a user to add as member to team.', availableUsers)
                            .then((chosenUsers) {
                              /// TODO pickup chosen users and update _membersWidget
                              //_members = users;
                              //print("USERS: " + chosenUsers.toString());
                            });
                      }
                    ),
                ),
            ),
         ],
        ),
        Padding(
          padding: const EdgeInsets.only(top: 10.0, left: 10.0, right: 10.0),
          child: Column(children: _membersWidget),
        ),
      ],
    );
  }

  void _createUI() {
    _membersWidget = List<Container>();
    _members.forEach((element) {
      _membersWidget.add(Container(
        child:
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(element.realName),
              CircleButton.create(24, Icons.remove, 16, () {
                /// TODO
                DialogModal(context).show('Under Construction', 'TODO: Remove team member....', false);
              }),
            ]
          )
        ),
      );
    });
    setState(() {});
  }
}
