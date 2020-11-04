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
import 'package:TaskTracker/service/service.task.dart';
import 'package:TaskTracker/service/task.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';


class WidgetTaskList extends StatefulWidget {
  WidgetTaskList({Key key, this.title = 'Tasks'}) : super(key: key);

  final String title;

  @override
  _WidgetTaskListState createState() => _WidgetTaskListState();
}

class _WidgetTaskListState extends State<WidgetTaskList> {

  bool _stateReady = false;
  final _serviceTask = ServiceTask();
  PaginatedDataTable _dataTable;
  List<Task> _tasks = [];
  bool _sortAscending = true;

  @override
  void initState() {
    super.initState();
    _retrieveTasks();
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
          child:
          Column(
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

  void _addTask() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_TASK);
    _retrieveTasks();
  }

  void _deleteTask(int id, String name) async {
    var button = await DialogTwoButtonsModal(context)
        .show(Translator.text('Common', 'Attention'),
        Translator.text('WidgetTask', 'Do you really want to delete task \'') + name +'\'?',
        ButtonID.YES, ButtonID.NO);
    if (button != ButtonID.YES) {
      return;
    }

    _serviceTask
      .deleteTask(id)
      .then((status) {
          DialogModal(context).show(Translator.text('WidgetTask', 'Task Deletion'),
              Translator.text('WidgetTask', 'Task was successfully deleted.'), false);
          _retrieveTasks();
        },
        onError: (err) {
          print(Translator.text('WidgetTask', 'Failed to delete task, reason: ') + err.toString());
      });
  }

  void _sortTasks(bool ascending) {
    _tasks.sort((taskA, taskB) => taskA.title?.compareTo(taskB?.title));
    if (!ascending) {
      _tasks = _tasks.reversed.toList();
    }
  }

  void _retrieveTasks() {
    _serviceTask
        .getTasks()
        .then((listTasks) {
            _tasks = listTasks;
            _sortTasks(_sortAscending);
            _updateState();
          },
          onError: (err) {
            print(Translator.text('WidgetTask', 'Failed to retrieve tasks, reason: ') + err.toString());
          });
  }

  PaginatedDataTable _createDataTable() {
    PaginatedDataTable dataTable = PaginatedDataTable(
      header: Text(''),
      columns: <DataColumn>[
        DataColumn(
          label: Text(
            Translator.text('Common', 'Title'),
            style: TextStyle(fontStyle: FontStyle.italic),
          ),
          onSort:(columnIndex, ascending) {
            setState(() {
              _sortAscending = !_sortAscending;
              _sortTasks(_sortAscending);
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
            Translator.text('Common', 'Teams'),
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
        CircleButton.create(24, Icons.add_circle_rounded, () => _addTask()),
      ],
    );

    return dataTable;
  }
}

class _DataProvider extends DataTableSource {

  _WidgetTaskListState parent;

  _DataProvider(this.parent);

  @override
  DataRow getRow(int index) {
    var teamNames = '';
    parent._tasks[index].teamNames.forEach((name) {
      teamNames += name + ' ';
    });

    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Container(constraints: BoxConstraints(maxWidth: 100), child: Text(parent._tasks[index].title))),
        DataCell(Container(constraints: BoxConstraints(maxWidth: 250), child: Text(parent._tasks[index].description))),
        DataCell(Text(teamNames)),
        DataCell(
          Row(
            children: [
              Spacer(),
              Padding(
                padding: EdgeInsets.all(4.0),
                child:
                  CircleButton.create(24, Icons.edit, () {
                    Navigator.pushNamed(parent.context, NavigationLinks.NAV_EDIT_TASK, arguments: parent._tasks[index].id)
                        .then((value) {
                            if (value != ButtonID.CANCEL) {
                              parent._retrieveTasks();
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
                          () => parent._deleteTask(parent._tasks[index].id, parent._tasks[index].title)
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
  int get rowCount => parent._tasks.length;

  @override
  int get selectedRowCount => 0;
}
