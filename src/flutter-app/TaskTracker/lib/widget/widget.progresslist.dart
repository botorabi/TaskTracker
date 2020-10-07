/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/common/calendar.utils.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/dialog/dialogtwobuttons.modal.dart';
import 'package:TaskTracker/navigation.links.dart';
import 'package:TaskTracker/service/progress.dart';
import 'package:TaskTracker/service/service.progress.dart';
import 'package:flutter/material.dart';


class WidgetProgressList extends StatefulWidget {
  WidgetProgressList({Key key, this.title = 'Progress'
      ''}) : super(key: key);

  final String title;
  final _WidgetProgressListState _widgetProgressListState = _WidgetProgressListState();

  @override
  _WidgetProgressListState createState() => _widgetProgressListState;
}

class _WidgetProgressListState extends State<WidgetProgressList> {

  final _serviceProgress = ServiceProgress();
  PaginatedDataTable _dataTable;
  List<Progress> _progresses = [];
  bool _sortAscending = false;

  @override
  void initState() {
    super.initState();
    _retrieveProgresses();
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
          child: Column(
            children: [
              Text(widget.title),
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: SizedBox(
                  width: constraints.maxWidth,
                  child: _dataTable,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _addProgress() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_PROGRESS);
    _retrieveProgresses();
  }

  void _deleteProgress(int id) async {
    var button = await DialogTwoButtonsModal(context)
        .show('Attention', "You really want to delete the progress entry?", ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceProgress
      .deleteProgress(id)
      .then((status) {
          DialogModal(context).show('Progress Deletion', 'Progress entry was successfully deleted.', false);
          _retrieveProgresses();
        },
        onError: (err) {
          print('Failed to delete progress entry, reason: ' + err.toString());
      });
  }

  void _sortProgress(bool ascending) {
    _progresses.sort((progressA, progressB) => progressA.reportWeek?.compareTo(progressB?.reportWeek));
    if (!ascending) {
      _progresses = _progresses.reversed.toList();
    }
  }

  void _retrieveProgresses() {
    _serviceProgress
        .getAllProgress()
        .then((listTasks) {
            _progresses = listTasks;
            _sortProgress(_sortAscending);
            setState(() {});
          },
          onError: (err) {
            print("Failed to retrieve progress entries, reason: " + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    String currentWeek = CalendarUtils.getCurrentCalendarWeek().toString();
    String currentYear = CalendarUtils.getCurrentCalendarYear().toString();
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text('Current Calendar Week ' + currentYear + ' / ' + currentWeek),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            'Title',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Calendar Week',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort:(columnIndex, ascending) {
            setState(() {
              _sortAscending = !_sortAscending;
              _sortProgress(_sortAscending);
              _dataTable = _createDataTable();
            });
          },
        ),
        DataColumn(
          label: Text(''),
        ),
      ],
      rowsPerPage: 10,
      onRowsPerPageChanged: null,
      source: _DataProvider(this),
      sortColumnIndex: 0,
      sortAscending: _sortAscending,
      actions: [
        CircleButton.create(24, Icons.add_box_rounded, () => _addProgress(), "Add New Progress"),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetProgressListState parent;

  _DataProvider(this.parent);

  @override
  DataRow getRow(int index) {
    bool modifiable = Config.authStatus.isAdmin() ||
        CalendarUtils.checkCurrentWeekDistance(parent._progresses[index].reportWeek, parent._progresses[index].reportYear);

    String userName = (Config.authStatus.isAdmin() || Config.authStatus.isTeamLead()) ? (' [' + parent._progresses[index].ownerName + ']') : '';

    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(parent._progresses[index].title + userName)),
        DataCell(Text(parent._progresses[index].reportYear.toString() + ' / ' + parent._progresses[index].reportWeek.toString())),
        DataCell(
          Row(
            children: [
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.visibility, () {
                    _showProgressEntry(parent._progresses[index]);
                  },
                  'View Progress'),
              ),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, !modifiable ? null : () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_PROGRESS, arguments: parent._progresses[index].id)
                        .then((value) {
                      if (value != ButtonID.CANCEL) {
                        parent._retrieveProgresses();
                      }
                     }
                    );
                  },
                  'Edit Progress'
                ),
              ),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.delete,
                    !modifiable ? null :  () => parent._deleteProgress(parent._progresses[index].id),
                    'Delete Progress'
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
  int get rowCount => parent._progresses.length;

  @override
  int get selectedRowCount => 0;

  void _showProgressEntry(Progress progress) {
    String text = 'Report Week ' + progress.reportWeek.toString() + ' / ' + progress.reportYear.toString() + '\n';
    text += '\nUser: ' + progress.ownerName + '\n';
    text += '\nText:\n\n' + progress.text;
    DialogModal(parent.context).show( progress.title, text, false);
  }
}
