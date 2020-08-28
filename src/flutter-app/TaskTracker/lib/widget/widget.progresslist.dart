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
import 'package:TaskTracker/service/progress.dart';
import 'package:TaskTracker/service/service.progress.dart';
import 'package:flutter/material.dart';


class WidgetProgressList extends StatefulWidget {
  WidgetProgressList({Key key, this.title = 'Progress'}) : super(key: key);

  final String title;
  final _WidgetProgressListState _widgetProgressListState = _WidgetProgressListState();

  @override
  _WidgetProgressListState createState() => _widgetProgressListState;
}

class _WidgetProgressListState extends State<WidgetProgressList> {

  final _serviceProgress = ServiceProgress();
  PaginatedDataTable _dataTable;
  List<Progress> _progresses = [];
  bool _sortAscending = true;

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
    return Card(
      elevation: 5,
      margin: EdgeInsets.all(10.0),
      child:
      SizedBox(
        child:
        Padding(
          padding: const EdgeInsets.all(0.0),
          child:
            Column(
            children: [
              Text(widget.title),
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: _dataTable,
              )
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
    _progresses.sort((progressA, progressB) => progressA.dateCreation?.compareTo(progressB?.dateCreation));
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
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text(''),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            'Title',
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
        ),
        DataColumn(
          label: Text(
            'Creation Date',
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
      rowsPerPage: 5,
      onRowsPerPageChanged: null,
      source: _DataProvider(this),
      sortColumnIndex: 0,
      sortAscending: _sortAscending,
      actions: [
        CircleButton.create(24, Icons.add, 16, () => _addProgress()),
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
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(parent._progresses[index].title)),
        DataCell(Text(parent._progresses[index].dateCreation.toIso8601String())),
        DataCell(
          Row(
            children: [
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, 16, () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_PROGRESS, arguments: parent._progresses[index].id)
                        .then((value) {
                            if (value != ButtonID.CANCEL) {
                              parent._retrieveProgresses();
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
                          () => parent._deleteProgress(parent._progresses[index].id)
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
}
