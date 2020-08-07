/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/common/datetime.formatter.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/dialog/dialogtwobuttons.modal.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/material.dart';


class WidgetUserList extends StatefulWidget {
  WidgetUserList({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _WidgetUserEditState createState() => _WidgetUserEditState();
}

class _WidgetUserEditState extends State<WidgetUserList> {

  final _serviceUser = ServiceUser();
  PaginatedDataTable _dataTable;
  List<UserInfo> _users = [];

  _WidgetUserEditState() {
    _retrieveUsers();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!Config.authStatus.isAdmin()) {
      print("ERROR: admin corner!");
      return Column();
    }
    else {
      _dataTable = _createDataTable();
      return SizedBox(
        width: Config.defaultPanelWidth,
        child: _dataTable,
      );
    }
  }

  void _addUser() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_USER);
    _retrieveUsers();
  }

  void _deleteUser(int id, String realName) async {
    var button = await DialogTwoButtonsModal(context)
        .show('Attention', "You really want to delete user '$realName'?", ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceUser
      .deleteUser(id)
      .then((status) {
          DialogModal(context).show('User Deletion', 'User was successfully deleted.', false);
          _retrieveUsers();
        },
        onError: (err) {
          print('Failed to delete user, reason: ' + err.toString());
      });
  }

  void _retrieveUsers() {
    _serviceUser
        .getUsers()
        .then((listUserInfo) {
            _users = listUserInfo;
            setState(() {});
          },
          onError: (err) {
            print("Failed to retrieve users, reason: " + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text("Users"),
      columns: const <DataColumn>[
        DataColumn(
          label: Text(
            'Name',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Login',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Last Login',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Roles',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(''),
        ),
      ],
      rowsPerPage: 5,
      onRowsPerPageChanged: null,
      source: _DataProvider(this),
      actions: [
        CircleButton.create(24, Icons.add, 16, () => _addUser()),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetUserEditState parent;

  _DataProvider(this.parent);

  @override
  DataRow getRow(int index) {
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(parent._users[index].realName)),
        DataCell(Text(parent._users[index].login)),
        DataCell(
          Text(
            DateAndTimeFormatter.formatDate(parent._users[index].lastLogin) +
                '\n' + DateAndTimeFormatter.formatTime(parent._users[index].lastLogin),
            textAlign: TextAlign.center
          ),
        ),
        DataCell(Text(parent._users[index].roles.join("\n").replaceAll(UserInfo.ROLE_PREFIX,''))),
        DataCell(
          Row(
            children: [
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, 16, () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_USER, arguments: parent._users[index].id)
                        .then((value) {
                            if (value != ButtonID.CANCEL) {
                              parent._retrieveUsers();
                            }
                          }
                        );
                  }
                ),
              ),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.delete, 16,
                    (parent._users[index].id == Config.authStatus.userId) ?
                    null : () => parent._deleteUser(parent._users[index].id, parent._users[index].realName)
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  @override
  bool get isRowCountApproximate => false;

  @override
  int get rowCount => parent._users.length;

  @override
  int get selectedRowCount => 0;
}
