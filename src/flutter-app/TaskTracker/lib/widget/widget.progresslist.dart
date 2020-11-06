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
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetProgressList extends StatefulWidget {
  WidgetProgressList({Key key, this.title = 'Progress'}) : super(key: key);

  final String title;
  final _WidgetProgressListState _widgetProgressListState = _WidgetProgressListState();

  @override
  _WidgetProgressListState createState() => _widgetProgressListState;
}

class _WidgetProgressListState extends State<WidgetProgressList> {

  static const MAX_ROWS_PER_PAGE = 10;

  final _serviceProgress = ServiceProgress();
  PaginatedDataTable _dataTable;
  _DataProvider _dataProvider;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    _dataProvider = _DataProvider(this, MAX_ROWS_PER_PAGE);
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
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_PROGRESS)
        .then((value) {
          if (value != ButtonID.CANCEL) {
            _dataProvider.updateCurrentPage();
          }
    });
  }

  void _deleteProgress(int id) async {
    var button = await DialogTwoButtonsModal(context)
        .show(Translator.text('Common', 'Attention'),
              Translator.text('WidgetProgressList', 'Do you really want to delete the progress entry?'),
              ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceProgress
      .deleteProgress(id)
      .then((status) {
          DialogModal(context).show(Translator.text('WidgetProgressList', 'Progress Deletion'),
              Translator.text('WidgetProgressList', 'Progress entry was successfully deleted.'), false)
              .then((value) { _dataProvider.updateCurrentPage(); });
        },
        onError: (err) {
          print(Translator.text('WidgetProgressList','Failed to delete progress entry, reason: ') + err.toString());
      });
  }

  PaginatedDataTable _createDataTable() {
    String currentWeek = CalendarUtils.getCurrentCalendarWeek().toString();
    String currentYear = CalendarUtils.getCurrentCalendarYear().toString();
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text(Translator.text('WidgetProgressList', 'Progress Entries') + '(' +
                    Translator.text('WidgetProgressList', 'Current Calendar Week') + ': ' + currentYear + ' / ' + currentWeek + ')'),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            Translator.text('Common', 'Title'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            Translator.text('Common', 'Calendar Week'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(''),
        ),
      ],
      rowsPerPage: MAX_ROWS_PER_PAGE,
      source: _dataProvider,
      sortColumnIndex: 0,
      actions: [
        CircleButton.create(24, Icons.add_box_rounded, () => _addProgress(), Translator.text('WidgetProgressList', 'Add New Progress Entry')),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {
  _WidgetProgressListState parent;
  int maxRowCount;

  int  _currentPage = -1;
  int  _rowCount = 1;
  bool _fetchingData = false;
  List<Progress> _progresses = [];

  _DataProvider(this.parent, this.maxRowCount);

  void sortProgress(bool ascending) {
    _progresses.sort((progressA, progressB) => progressA.reportWeek?.compareTo(progressB?.reportWeek));
    if (!ascending) {
      _progresses = _progresses.reversed.toList();
    }
  }

  void updateCurrentPage() {
    _fetchEntries(_currentPage);
  }

  void _fetchEntries(int page) async {
    _fetchingData = true;
    parent._serviceProgress
        .getPagedProgress(page, maxRowCount)
        .then((progressPaged) {
          _rowCount = progressPaged.totalCount;
          _currentPage = progressPaged.currentPage;
          _progresses = progressPaged.progressList;
          _fetchingData = false;
          notifyListeners();
        },
        onError: (err) {
          print("Failed to retrieve progress entries, reason: " + err.toString());
          _fetchingData = false;
        });
  }

  @override
  DataRow getRow(int index) {
    if (_fetchingData) {
      return null;
    }

    int minIndex = _currentPage * maxRowCount;
    int maxIndex = (_currentPage + 1) * maxRowCount - 1;
    if ((_currentPage < 0) || (index < minIndex) || (index > maxIndex)) {
      int page = (index / maxRowCount).floor();
      _fetchEntries(page);
      return null;
    }

    index = (index % maxRowCount);

    bool modifiable = Config.authStatus.isAdmin() ||
        CalendarUtils.checkCurrentWeekDistance(_progresses[index].reportWeek, _progresses[index].reportYear);

    String userName = (Config.authStatus.isAdmin() || Config.authStatus.isTeamLead()) ? (' [' + _progresses[index].ownerName + ']') : '';

    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Container(constraints: BoxConstraints(maxWidth: 350), child: Text(_progresses[index].title + userName))),
        DataCell(Container(constraints: BoxConstraints(maxWidth: 90), child: Text(_progresses[index].reportYear.toString() + ' / ' + _progresses[index].reportWeek.toString()))),
        DataCell(
          Row(
            children: [
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.visibility, () {
                    _showProgressEntry(_progresses[index]);
                  },
                  Translator.text('Common', 'View')),
              ),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, !modifiable ? null : () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_PROGRESS, arguments: _progresses[index].id)
                        .then((value) {
                      if (value != ButtonID.CANCEL) {
                        _fetchEntries(_currentPage);
                      }
                     }
                    );
                  },
                  Translator.text('Common', 'Edit'),
                ),
              ),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.delete,
                    !modifiable ? null :  () => parent._deleteProgress(_progresses[index].id),
                    Translator.text('Common', 'Delete'),
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
  int get rowCount => _rowCount;

  @override
  int get selectedRowCount => 0;

  void _showProgressEntry(Progress progress) {
    String text = Translator.text('Common', 'Calendar Week') + ' ' + progress.reportWeek.toString() + ' / ' + progress.reportYear.toString() + '\n';
    text += '\n' + Translator.text('Common', 'User') + ': ' + progress.ownerName + '\n';
    text += '\n' + Translator.text('Common', 'Text') + ':\n\n' + progress.text;
    DialogModal(parent.context).show( progress.title, text, false);
  }
}
