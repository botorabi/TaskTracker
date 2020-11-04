/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/common/utf8.utils.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/dialog/dialogtwobuttons.modal.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/service.team.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetTeamList extends StatefulWidget {
  WidgetTeamList({Key key, this.title = 'Teams'}) : super(key: key);

  final String title;

  @override
  _WidgetTeamListState createState() => _WidgetTeamListState();
}

class _WidgetTeamListState extends State<WidgetTeamList> {

  bool _stateReady = false;
  final _serviceTeam = ServiceTeam();
  PaginatedDataTable _dataTable;
  List<Team> _teams = [];
  bool _sortAscending = true;

  @override
  void initState() {
    super.initState();
    _retrieveTeams();
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

  void _addTeam() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_TEAM);
    _retrieveTeams();
  }

  void _deleteTeam(int id, String name) async {
    var button = await DialogTwoButtonsModal(context)
        .show(Translator.text('Common', 'Attention'), Translator.text('WidgetTeam', 'Do you really want to delete team "') + name  + '"?',
        ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceTeam
      .deleteTeam(id)
      .then((status) {
          DialogModal(context).show(Translator.text('WidgetTeam', 'Team Deletion'), Translator.text('WidgetTeam', 'Team was successfully deleted.'), false);
          _retrieveTeams();
        },
        onError: (err) {
          print(Translator.text('WidgetTeam', 'Failed to delete team, reason: ') + err.toString());
      });
  }

  void _sortTeams(bool ascending) {
    _teams.sort((teamA, teamB) => teamA.name?.compareTo(teamB?.name));
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
            _updateState();
          },
          onError: (err) {
            print(Translator.text('WidgetTeam', 'Failed to retrieve teams, reason: ') + err.toString());
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
              _sortTeams(_sortAscending);
              _dataTable = _createDataTable();
            });
          },
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Description'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Team Lead'),
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
        Visibility(
          visible: Config.authStatus.isAdmin(),
          child: CircleButton.create(24, Icons.add_circle_rounded, () => _addTeam(), Translator.text('WidgetTeam', 'Add New Team')),
        ),
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
    String teamLeadNames = '';
    parent._teams[index].teamLeaderNames.forEach((userName) {
      teamLeadNames += userName + ' ';
    });

    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Container(constraints: BoxConstraints(maxWidth: 100), child: Text(parent._teams[index].name))),
        DataCell(Container(constraints: BoxConstraints(maxWidth: 250), child: Text(parent._teams[index].description))),
        DataCell(Container(constraints: BoxConstraints(maxWidth: 200), child: Text(teamLeadNames))),
        DataCell(
          Row(
            children: [
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(20, Icons.edit, () {
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
              Visibility(
                visible: Config.authStatus.isAdmin(),
                child: Padding(
                  padding: EdgeInsets.all(4.0),
                  child:
                    CircleButton.create(20, Icons.delete,
                            () => parent._deleteTeam(parent._teams[index].id, parent._teams[index].name)
                  ),
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
