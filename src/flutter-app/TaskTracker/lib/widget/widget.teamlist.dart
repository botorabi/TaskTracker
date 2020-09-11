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
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/dialog/dialogtwobuttons.modal.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:flutter/material.dart';


class WidgetTeamList extends StatefulWidget {
  WidgetTeamList({Key key, this.title = 'Teams'}) : super(key: key);

  final String title;
  final _WidgetTeamListState _widgetTeamListState = _WidgetTeamListState();

  @override
  _WidgetTeamListState createState() => _widgetTeamListState;

  WidgetTeamList setExpanded(bool expanded) {
    _widgetTeamListState.setExpanded(expanded);
    return this;
  }
}

class _WidgetTeamListState extends State<WidgetTeamList> {

  final _serviceTeam = ServiceTeam();
  PaginatedDataTable _dataTable;
  List<Team> _teams = [];
  bool _expanded = false;
  bool _sortAscending = true;

  @override
  void initState() {
    super.initState();
    _retrieveTeams();
  }

  void setExpanded(bool expanded) {
    _expanded = expanded;
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
      return Card(
        elevation: 5,
        margin: EdgeInsets.all(10.0),
        child:
        SizedBox(
          child:
          Padding(
            padding: const EdgeInsets.all(0.0),
            child:
            ExpansionTile(
                title: Text(widget.title),
                initiallyExpanded: _expanded,
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: _dataTable,
                  )
                ]
            ),
          ),
        ),
      );
    }
  }

  void _addTeam() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_TEAM);
    _retrieveTeams();
  }

  void _deleteTeam(int id, String name) async {
    var button = await DialogTwoButtonsModal(context)
        .show('Attention', "You really want to delete team '$name'?", ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceTeam
      .deleteTeam(id)
      .then((status) {
          DialogModal(context).show('Team Deletion', 'Team was successfully deleted.', false);
          _retrieveTeams();
        },
        onError: (err) {
          print('Failed to delete team, reason: ' + err.toString());
      });
  }

  void _sortTeams(bool ascending) {
    _teams.sort((userInfoA, userInfoB) => userInfoA.name?.compareTo(userInfoB?.name));
    if (!ascending) {
      _teams = _teams.reversed.toList();
    }
  }

  void _retrieveTeams() {
    _serviceTeam
        .getTeams()
        .then((listTeam) {
            _teams = listTeam;
            _sortTeams(_sortAscending);
            setState(() {});
          },
          onError: (err) {
            print("Failed to retrieve teams, reason: " + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text(''),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            'Name',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort:(columnIndex, ascending) {
            setState(() {
              _sortAscending = !_sortAscending;
              _sortTeams(_sortAscending);
              _dataTable = _createDataTable();
            });
          },
        ),
        DataColumn(
          label: Text(
            'Description',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Active',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Members',
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
        CircleButton.create(24, Icons.add, () => _addTeam()),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetTeamListState parent;

  _DataProvider(this.parent);

  @override
  DataRow getRow(int index) {
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(parent._teams[index].name)),
        DataCell(Text(parent._teams[index].description)),
        DataCell(Text(parent._teams[index].active ? 'Yes' : 'No')),
        DataCell(Text(parent._teams[index].users?.length.toString())),
        DataCell(
          Row(
            children: [
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_TEAM, arguments: parent._teams[index].id)
                        .then((value) {
                            if (value != ButtonID.CANCEL) {
                              parent._retrieveTeams();
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
                          () => parent._deleteTeam(parent._teams[index].id, parent._teams[index].name)
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
  int get rowCount => parent._teams.length;

  @override
  int get selectedRowCount => 0;
}
