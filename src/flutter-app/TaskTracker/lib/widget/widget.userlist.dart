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
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetUserList extends StatefulWidget {
  WidgetUserList({Key key, this.title = 'Users'}) : super(key: key);

  final String title;

  @override
  _WidgetUserListState createState() => _WidgetUserListState();
}

class _WidgetUserListState extends State<WidgetUserList> {

  bool _stateReady = false;
  final _serviceUser = ServiceUser();
  PaginatedDataTable _dataTable;
  List<UserInfo> _users = [];
  bool _sortAscending = true;

  @override
  void initState() {
    super.initState();
    _retrieveUsers();
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
    _dataTable = _createDataTable();
    _stateReady = true;
    return LayoutBuilder(
      builder: (context, constraints) => SingleChildScrollView(
        child: Card(
          elevation: 5,
          margin: EdgeInsets.all(10.0),
          child: Column(
            children: [
              Visibility(
                visible: widget.title != null && widget.title != '',
                child: Padding(
                  padding: const EdgeInsets.only(top: 20.0),
                  child: Text(widget.title, style: TextStyle(fontWeight: FontWeight.bold)),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: SizedBox(
                  width: constraints.maxWidth,
                  child: _dataTable,
                ),
              )
            ]
          ),
        ),
      ),
    );
  }

  void _addUser() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_USER);
    _retrieveUsers();
  }

  void _deleteUser(int id, String realName) async {
    var button = await DialogTwoButtonsModal(context)
        .show(Translator.text('Common', 'Attention'), Translator.text('WidgetUser', 'Do you really want to delete user \'') + realName + '\'?',
        ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceUser
      .deleteUser(id)
      .then((status) {
          DialogModal(context).show(Translator.text('WidgetUser', 'User Deletion'), Translator.text('WidgetUser', 'User was successfully deleted.'), false);
          _retrieveUsers();
        },
        onError: (err) {
          print(Translator.text('WidgetUser', 'Failed to delete user, reason: ') + err.toString());
      });
  }

  void _sortUsers(bool ascending) {
    _users.sort((userInfoA, userInfoB) => userInfoA.realName?.compareTo(userInfoB?.realName));
    if (!ascending) {
      _users = _users.reversed.toList();
    }
  }

  void _retrieveUsers() {
    _serviceUser
        .getUsers()
        .then((listUserInfo) {
            _users = listUserInfo;
            _sortUsers(_sortAscending);
            _updateState();
          },
          onError: (err) {
            print(Translator.text('WidgetUser', 'Failed to retrieve users, reason: ') + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text(''),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            Translator.text('Common', 'Name'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort:(columnIndex, ascending) {
            setState(() {
              _sortAscending = !_sortAscending;
              _sortUsers(_sortAscending);
              _dataTable = _createDataTable();
            });
          },
        ),
        DataColumn(
          label: Text(
            Translator.text('WidgetUser', 'Login'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            Translator.text('WidgetUser', 'Last Login'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            Translator.text('WidgetRoles', 'Roles'),
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
      sortColumnIndex: 0,
      sortAscending: _sortAscending,
      actions: [
        CircleButton.create(24, Icons.add_circle_rounded, () => _addUser()),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetUserListState parent;

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
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, () {
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
                  CircleButton.create(24, Icons.delete,
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
