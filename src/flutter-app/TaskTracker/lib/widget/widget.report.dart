/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:flutter/material.dart';


class WidgetTeamReport extends StatefulWidget {
  WidgetTeamReport({Key key, this.title = 'Team Report'}) : super(key: key);

  final String title;
  final _WidgetTeamReportState _widgetTeamReportState = _WidgetTeamReportState();

  @override
  _WidgetTeamReportState createState() => _widgetTeamReportState;

  WidgetTeamReport setExpanded(bool expanded) {
    _widgetTeamReportState.setExpanded(expanded);
    return this;
  }
}

class _WidgetTeamReportState extends State<WidgetTeamReport> {

  final _serviceUser = ServiceUser();
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
    _dataTable = _createDataTable();
    return LayoutBuilder(
      builder: (context, constraints) => SingleChildScrollView(
        child: Card(
          elevation: 5,
          margin: EdgeInsets.all(10.0),
          child:
          ExpansionTile(
              title: Text(widget.title),
              initiallyExpanded: _expanded,
              children: <Widget>[
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

  void _sortTeams(bool ascending) {
    _teams.sort((userInfoA, userInfoB) => userInfoA.name?.compareTo(userInfoB?.name));
    if (!ascending) {
      _teams = _teams.reversed.toList();
    }
  }

  void _retrieveTeams() {
    _serviceUser
        .getUserTeams()
        .then((listTeam) {
            _teams = List<Team>();
            listTeam.forEach((team) {
              if (team.active) {
                _teams.add(team);
              }
            });
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
            'Team Lead',
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
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetTeamReportState parent;

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
        DataCell(Text(parent._teams[index].name)),
        DataCell(Text(parent._teams[index].description)),
        DataCell(Text(teamLeadNames)),
        DataCell(
          Row(
            children: [
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.file_download, () {
                    //! TODO
                    DialogModal(parent.context).show("Attention", "Under Construction!", false);
                  }
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
