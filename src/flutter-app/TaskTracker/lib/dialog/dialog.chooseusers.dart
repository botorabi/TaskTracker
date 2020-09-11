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
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

/*
 Dialog for choosing users.

 Example for usage:

  DialogChooseUsers(context)
    .show('Choose User', 'Choose users to add as members to team.')
    .then((chosenUsers) => chosenUsers.forEach((user) => print("CHOSEN USER: " + user.realName)));
 */
class DialogChooseUsers {

  BuildContext _context;
  final _serviceUser = ServiceUser();
  final _chosenUsers = List<UserInfo>();
  final _controller = ScrollController();
  dynamic _setState;
  List<Container> _listCandidates = List<Container>();
  List<Container> _listChosenUsers = List<Container>();

  DialogChooseUsers(this._context);

  Future<List<UserInfo>> show(String title, String text) async {
    return showDialog(
        context: _context,
        builder: (context) {
          return StatefulBuilder(
            builder: (context, setState) {
              return AlertDialog(
                title: Text(title),
                content: _createUsersUI(text, setState),
                actions: <Widget>[
                  FlatButton(
                    child: Text(ButtonID.CANCEL),
                    onPressed: () {
                      Navigator.of(_context).pop(List<UserInfo>());
                    },
                  ),
                  FlatButton(
                    child: Text(ButtonID.CHOOSE),
                    onPressed: () {
                      Navigator.of(_context).pop(_chosenUsers);
                    },
                  ),
                ],
              );
            },
          );
        },
      );
  }

  SizedBox _createUsersUI(String text, setState) {
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
                Text('Find User',
                    style: TextStyle(fontWeight: FontWeight.w500)
                ),
                TextField(
                  decoration: InputDecoration(
                      hintText: 'Enter at least 3 characters'
                  ),
                  onChanged: (value) {
                    _searchUser(value).then((value) {
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
                        child: Text('Found Users',
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
                            border: Border.all(color: Config.listBorderColor),
                            color: Config.listBackgroundColor,
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
                        child: Text('Chosen Users',
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
                            border: Border.all(color: Config.listBorderColor),
                            color: Config.listBackgroundColor,
                          ),
                          child: ListView(
                            controller: _controller,
                            shrinkWrap: true,
                            children: <Widget>[... _listChosenUsers],
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

  void _createCandidates(List<UserInfo> users) {
    _listCandidates.clear();
    users.forEach((userInfo) {
      _listCandidates.add(
        Container(
          child:
            ListTile(
              title: Text(userInfo.realName, style: TextStyle(fontWeight: FontWeight.w600)),
              trailing: CircleButton.create(16, Icons.arrow_right, () {
                if (_addNewChosenUser(userInfo)) {
                  _updateChosenUsersUI();
                  _scrollToLastChosenUser();
                }
              }),
          ),
        ),
      );
    });
  }

  void _updateChosenUsersUI() {
    _listChosenUsers.clear();
    _chosenUsers.forEach((userInfo) {
      _listChosenUsers.add(
        Container(
          child:
          ListTile(
              title: Text(userInfo.realName, style: TextStyle(fontWeight: FontWeight.w600)),
              trailing: CircleButton.create(16, Icons.remove, () {
                _chosenUsers.remove(userInfo);
                _updateChosenUsersUI();
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

  bool _addNewChosenUser(UserInfo userInfo) {
    bool userIsInList = false;
    _chosenUsers.forEach((user) {
      if (user.id == userInfo.id) {
        userIsInList = true;
      }
    });
    if (!userIsInList) {
      _chosenUsers.add(userInfo);
    }
    return !userIsInList;
  }

  void _scrollToLastChosenUser() {
    _controller.animateTo(_controller.position.maxScrollExtent + 50,
        duration: Duration(seconds: 1),
        curve: Curves.fastOutSlowIn);
  }

  Future<void> _searchUser(String value) async {
    if (value.length > 2) {
      await _serviceUser.searchUser(value)
          .then((users) {
            _createCandidates(users);
            return Future<void>.value(users.length);
      });
    }
    else {
      _listCandidates = List<Container>();
    }
  }
}
