/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.report.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/widget/widget.calendarweek.dart';
import 'package:flutter/material.dart';


class WidgetTeamReport extends StatefulWidget {
  WidgetTeamReport({Key key, this.title = 'Team Progress Report'}) : super(key: key);

  final String title;
  final _WidgetTeamReportState _widgetTeamReportState = _WidgetTeamReportState();

  @override
  _WidgetTeamReportState createState() => _widgetTeamReportState;
}

class _WidgetTeamReportState extends State<WidgetTeamReport> {

  final _serviceUser = ServiceUser();
  final _serviceReport = ServiceReport();

  PaginatedDataTable _dataTable;
  List<Team> _teams = [];
  List<bool> _selectedTeams = List<bool>();
  bool _sortAscending = true;
  WidgetCalendarWeek _widgetCalendarWeekFrom = WidgetCalendarWeek(title: 'From');
  WidgetCalendarWeek _widgetCalendarWeekTo = WidgetCalendarWeek(title: 'To',);

  @override
  void initState() {
    super.initState();
    _retrieveTeams();
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
          Column(
            children: [
              Center(
              child:
                Padding(
                  padding: EdgeInsets.only(top: 20.0, bottom: 20),
                  child:
                    Text(widget.title,
                      style: TextStyle(fontSize: 20)
                    ),
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20.0, right: 20, left: 20),
                child:
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Card(
                        elevation: 1,
                        child:
                        Padding(
                          padding: const EdgeInsets.all(20.0),
                          child:
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('Report Period',
                                textAlign: TextAlign.left,
                                style: TextStyle(fontWeight: FontWeight.w400, fontSize: 18),
                              ),
                              SizedBox(height: 20),
                              Row(
                                children: [
                                  _widgetCalendarWeekFrom,
                                  SizedBox(width: 30),
                                  _widgetCalendarWeekTo,
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: SizedBox(
                    width: constraints.maxWidth,
                    child: _dataTable,
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 0.0, bottom: 15.0, right: 25.0),
                  child: Row(
                    children: [
                      Spacer(),
                      RaisedButton(
                      child: Text(ButtonID.CREATE),
                        onPressed: () {
                          _createReport();
                        },
                      ),
                    ],
                  ),
                ),
              ]
          ),
        ),
      ),
    );
  }

  void _createReport() async {
    DateTime fromDate = DateTime(_widgetCalendarWeekFrom.getYear(), 1,
        ((_widgetCalendarWeekFrom.getWeek() - 1) * 7));

    DateTime toDate = DateTime(_widgetCalendarWeekTo.getYear(), 1,
        ((_widgetCalendarWeekTo.getWeek()) * 7));

    if (fromDate.isAfter(toDate)) {
      DialogModal(context).show("Attention", "Please choose a date for 'From' less than 'To'.", true);
      return;
    }

    String fileName = "Report.pdf";
    String teamNames = "Teams: ";
    List<int> teamIDs = List<int>();
    for (int i = 0; i < _selectedTeams.length; i++) {
      if (_selectedTeams[i]) {
        teamIDs.add(_teams[i].id);
        teamNames += "'" + _teams[i].name + "' ";
      }
    }

    bool success = await _serviceReport.createReportDocument(teamIDs, fromDate, toDate, 'Progress Report', teamNames, fileName);
    if (!success) {
      DialogModal(context).show("Attention", "Report document could not be created!", true);
    }
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
            _selectedTeams = List<bool>.generate(_teams.length, (index) => false);
            setState(() {});
          },
          onError: (err) {
            print("Failed to retrieve teams, reason: " + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text('Select Teams for Report Creation'),
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

  void onRowSelectionChanged(int row, bool state) {
    setState(() {
      _selectedTeams[row] = state;
    });
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
          Padding(
            padding: EdgeInsets.all(4.0),
            child:
              Checkbox(
                value: parent._selectedTeams[index],
                onChanged: (bool value) {
                  parent.onRowSelectionChanged(index, value);
                }
            ),
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
