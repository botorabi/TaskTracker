
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.report.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../config.dart';
import '../translator.dart';

abstract class ReportTableManager extends StatefulWidget{

  ReportDataProvider getDataProvider();
}

abstract class ReportDataProvider extends DataTableSource {

  ReportSortType _sortType = ReportSortType.REPORT_SORT_TYPE_NONE;

  void _onTableSort(int sortColumn, bool ascending);

  void setReportSort(ReportSortType sortType) {
    _sortType = sortType;
  }

  Future<void> createReport(BuildContext context, DateTime fromDate, DateTime toDate) async {}
}

abstract class _TableState<T> extends State<ReportTableManager> {

  bool _sortAscending = true;

  int _sortColumn = 0;

  void _onSort(int columnIndex, bool ascending) {
    setState((){
      _sortAscending = ascending;
      _sortColumn = columnIndex;
      widget.getDataProvider()._onTableSort(_sortColumn, _sortAscending);
    });
  }
}

class _TeamDataProvider extends ReportDataProvider {

  final _serviceUser = ServiceUser();

  final _serviceReport = ServiceReport();

  List<Team> _teams = [];
  List<bool> _selectedTeams = List<bool>.empty(growable: true);

  _TeamDataProvider() {
    _retrieveTeams(true);
    _sortType = ReportSortType.REPORT_SORT_TYPE_USER;
  }

  String _getTeamLeadNames(Team team) {
    String teamLeadNames = '';
    if (_teams.length >= 1) {
      team.teamLeaderNames.forEach((userName) {
        teamLeadNames += userName + ' ';
      });
    }
    return teamLeadNames;
  }

  @override
  void _onTableSort(int sortColumn, bool ascending) {
    if (sortColumn == 0) {
      _teams.sort(
              (teamA, teamB) => teamA.name?.compareTo(teamB?.name));
    } else if (sortColumn == 1) {
      _teams.sort(
              (teamA, teamB) => teamA.description?.compareTo(teamB?.description));
    } else if (sortColumn == 2) {
      _teams.sort(
              (teamA, teamB) => _getTeamLeadNames(teamA).compareTo(_getTeamLeadNames(teamB)));
    }
    if (!ascending) {
      _teams = _teams.reversed.toList();
    }
    for (int i = 0; i < _selectedTeams.length; ++i) {
      _selectedTeams[i] = false;
    }
    notifyListeners();
  }

  void _retrieveTeams(bool ascending) {
    _serviceUser
        .getUserTeams()
        .then((listTeam) {
      _teams = List<Team>.empty(growable: true);
      listTeam.forEach((team) {
        if (team.active) {
          _teams.add(team);
        }
      });
      _selectedTeams = List<bool>.generate(_teams.length, (index) => false);
      notifyListeners();
    },
        onError: (err) {
          print("Failed to retrieve teams, reason: " + err.toString());
        });
  }

  @override
  DataRow getRow(int index) {
    String teamLeadNames = _getTeamLeadNames(_teams[index]);

    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(_teams[index].name)),
        DataCell(Text(_teams[index].description)),
        DataCell(Text(teamLeadNames)),
        DataCell(
          Padding(
            padding: EdgeInsets.all(4.0),
            child:
            Checkbox(
                value: _selectedTeams[index],
                onChanged: (bool value) {
                  _selectedTeams[index] = value;
                  notifyListeners();
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
  int get rowCount => _teams.length;

  @override
  int get selectedRowCount => 0;

  @override
  Future<void> createReport(BuildContext context, DateTime fromDate, DateTime toDate) async {

    if (fromDate.isAfter(toDate) || (fromDate.millisecondsSinceEpoch == toDate.millisecondsSinceEpoch)) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReport', 'Please choose a date for \'From\' less than \'To\'.'), true);
      return;
    }

    String fileName = Translator.text('Common', 'Report') + ".pdf";
    String teamNames = Translator.text('Common', 'Teams') + ': ';
    List<int> teamIDs = List<int>.empty(growable: true);
    int countSelectedTeams = 0;
    for (int i = 0; i < _selectedTeams.length; i++) {
      if (_selectedTeams[i]) {
        countSelectedTeams++;
        teamIDs.add(_teams[i].id);
        teamNames += "'" + _teams[i].name + "' ";
      }
    }

    if (countSelectedTeams < 1) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReport', 'Please select at least one team.'), true);
      return;
    }

    bool success = await _serviceReport.createTeamReportDocument(teamIDs, fromDate, toDate,
        Translator.text('Common', 'Progress Report Team'), teamNames, fileName, _sortType);

    if (!success) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReport', 'Report document could not be created!'), true);
    }
  }
}

class TeamReportTableManager extends ReportTableManager {

  final _TeamDataProvider _provider;

  TeamReportTableManager()
      : _provider = _TeamDataProvider()
  {
  }

  @override
  ReportDataProvider getDataProvider() => _provider;

  @override
  _TeamReportTableManagerState createState() => _TeamReportTableManagerState();

}

class _TeamReportTableManagerState extends _TableState<TeamReportTableManager> {

  @override
  Widget build(BuildContext context) {
    return PaginatedDataTable(
      header: Text(
        Translator.text('WidgetReport', 'Select for Report Creation'),
        textAlign: TextAlign.left,
        style: TextStyle(fontWeight: FontWeight.w400, fontSize: 18),
      ),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            Translator.text('Common', 'Name'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort: _onSort,
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Description'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort: _onSort,
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Team Lead'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort: _onSort,
        ),
        DataColumn(
          label: Text(''),
        ),
      ],
      rowsPerPage: 5,
      onRowsPerPageChanged: null,
      source: widget.getDataProvider(),
      sortColumnIndex: _sortColumn,
      sortAscending: _sortAscending,
    );
  }
}

class _UserDataProvider extends ReportDataProvider {

  final _serviceUser = ServiceUser();

  final _serviceReport = ServiceReport();

  List<UserInfo> _users = [];
  List<bool> _selectedUsers = List<bool>.empty(growable: true);

  _UserDataProvider() {
    _retrieveUsers(true);
    _sortType = ReportSortType.REPORT_SORT_TYPE_USER;
  }

  @override
  void _onTableSort(int sortColumn, bool ascending) {
    if (sortColumn == 0) {
      _users.sort((userInfoA, userInfoB) =>
          userInfoA.realName?.compareTo(userInfoB?.realName));
    } else if (sortColumn == 1) {
      _users.sort((userInfoA, userInfoB) =>
          userInfoA.roles.join('\n').replaceAll(UserInfo.ROLE_PREFIX, '')?.compareTo(userInfoB?.roles?.join('\n')?.replaceAll(UserInfo.ROLE_PREFIX, '')));
    }
    if (!ascending) {
      _users = _users.reversed.toList();
    }
    for (int i = 0; i < _selectedUsers.length; ++i) {
      _selectedUsers[i] = false;
    }
    notifyListeners();
  }

  void _retrieveUsers(bool ascending) {
    _serviceUser
        .getUsers()
        .then(
            (listUserInfo) {
          _users = listUserInfo;
          _selectedUsers.clear();
          for (int i = 0; i < _users.length; ++i) {
            _selectedUsers.add(_users[i].id == Config.authStatus.userId);
          }
          notifyListeners();
        },
        onError: (err) {
          print(Translator.text('_UserDataProvider', 'Failed to retrieve users, reason: ') + err.toString());
        }
    );
  }

  @override
  DataRow getRow(int index) {
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Container(constraints: BoxConstraints(maxWidth: 100), child: Text(_users[index].realName))),
        DataCell(Container(constraints: BoxConstraints(maxWidth: 90), child: Text(_users[index].roles.join('\n').replaceAll(UserInfo.ROLE_PREFIX, '')))),
        DataCell(
          Padding(
            padding: EdgeInsets.all(4.0),
            child:
            Checkbox(
                value: _selectedUsers[index],
                onChanged: (bool value) {
                  for (int i = 0; i < _selectedUsers.length; i++) {
                    _selectedUsers[i] = false;
                  };
                  _selectedUsers[index] = value;
                  notifyListeners();
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
  int get rowCount => _users.length;

  @override
  int get selectedRowCount => 0;


  @override
  Future<void> createReport(BuildContext context, DateTime fromDate, DateTime toDate) async {

    if (fromDate.isAfter(toDate) || (fromDate.millisecondsSinceEpoch == toDate.millisecondsSinceEpoch)) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReport', 'Please choose a date for \'From\' less than \'To\'.'), true);
      return;
    }

    String fileName = Translator.text('Common', 'Report') + ".pdf";
    String userName = '';
    int userId;

    for (int i = 0; i < _selectedUsers.length; i++) {
      if (_selectedUsers[i]) {
        userId = _users[i].id;
        userName = _users[i].realName;
        break;
      }
      if (i == _selectedUsers.length - 1) {
        DialogModal(context).show(
            Translator.text('Common', 'Attention'),
            Translator.text('WidgetReport', 'Please select only one user!'), true);
        return;
      }
    }

    bool success = await _serviceReport.createUserReportDocument(userId, fromDate, toDate,
        Translator.text('Common', 'Progress Report'), userName, fileName, _sortType);

    if (!success) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReport', 'Report document could not be created!'), true);
    }
  }
}

class UserReportTableManager extends ReportTableManager {

  final _UserDataProvider _provider;

  UserReportTableManager()
      : _provider = _UserDataProvider()
  {
  }

  @override
  ReportDataProvider getDataProvider() => _provider;

  @override
  _UserReportTableManagerState createState() => _UserReportTableManagerState();
}

class _UserReportTableManagerState extends _TableState<UserReportTableManager> {

  @override
  Widget build(BuildContext context) {
    return PaginatedDataTable(
      header: Text(
        Translator.text('WidgetReport', 'Please select one user'),
        textAlign: TextAlign.left,
        style: TextStyle(fontWeight: FontWeight.w400, fontSize: 18),
      ),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            Translator.text('Common', 'Name'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort: _onSort,
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Roles'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort: _onSort,
        ),
        DataColumn(
          label: Text(''),
        ),
      ],
      rowsPerPage: 5,
      onRowsPerPageChanged: null,
      source: widget.getDataProvider(),
      sortColumnIndex: _sortColumn,
      sortAscending: _sortAscending,
    );
  }
}