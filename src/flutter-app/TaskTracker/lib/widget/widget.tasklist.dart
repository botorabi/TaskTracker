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
import 'package:flutter/material.dart';


class WidgetTaskList extends StatefulWidget {
  WidgetTaskList({Key key, this.title = 'Tasks'}) : super(key: key);

  final String title;
  final _WidgetTaskListState _widgetTaskListState = _WidgetTaskListState();

  @override
  _WidgetTaskListState createState() => _widgetTaskListState;

  WidgetTaskList setExpanded(bool expanded) {
    _widgetTaskListState.setExpanded(expanded);
    return this;
  }
}

class _WidgetTaskListState extends State<WidgetTaskList> {

  final _serviceTask = ServiceTask();
  PaginatedDataTable _dataTable;
  List<Task> _tasks = [];
  bool _expanded = false;
  bool _sortAscending = true;

  @override
  void initState() {
    super.initState();
    _retrieveTasks();
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

  void _addTask() async {
    await Navigator.pushNamed(context, NavigationLinks.NAV_NEW_TASK);
    _retrieveTasks();
  }

  void _deleteTask(int id, String name) async {
    var button = await DialogTwoButtonsModal(context)
        .show('Attention', "You really want to delete task '$name'?", ButtonID.YES, ButtonID.NO);

    if (button != ButtonID.YES) {
      return;
    }

    _serviceTask
      .deleteTask(id)
      .then((status) {
          DialogModal(context).show('Task Deletion', 'Task was successfully deleted.', false);
          _retrieveTasks();
        },
        onError: (err) {
          print('Failed to delete task, reason: ' + err.toString());
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
            setState(() {});
          },
          onError: (err) {
            print("Failed to retrieve tasks, reason: " + err.toString());
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
            'Description',
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
        CircleButton.create(24, Icons.add, () => _addTask()),
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
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(parent._tasks[index].title)),
        DataCell(Text(parent._tasks[index].description)),
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
