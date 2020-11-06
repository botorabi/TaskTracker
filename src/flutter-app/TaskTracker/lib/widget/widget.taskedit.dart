/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:io';

import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.task.dart';
import 'package:TaskTracker/service/task.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.taskaffiliates.dart';
import 'package:flutter/material.dart';


class WidgetTaskEdit extends StatefulWidget {
  WidgetTaskEdit({Key key, this.title, this.taskId}) : super(key: key);

  final String title;
  final int    taskId;

  @override
  _WidgetTaskEditState createState() => _WidgetTaskEditState(taskId: taskId);
}

class _WidgetTaskEditState extends State<WidgetTaskEdit> {

  int taskId;

  bool  _newTask;
  Task  _currentTask;
  final _serviceTask = ServiceTask();
  final _textEditingControllerTitle = TextEditingController();
  final _textEditingControllerDescription = TextEditingController();
  final _widgetAffiliates = WidgetTaskAffiliates();

  _WidgetTaskEditState({this.taskId = 0}) {
    _newTask = taskId == 0;
  }

  @override
  void initState() {
    super.initState();
    if (!_newTask) {
      _retrieveTask();
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4.0,
      margin: const EdgeInsets.all(30.0),
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: Config.DEFAULT_EDITOR_WIDTH),
        child: Column(
          children: [
            ListView(
              shrinkWrap: true,
              children: <Widget>[
                Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(top: 20.0),
                      child: Text(
                        Translator.text('WidgetTask', 'Edit Task Settings'),
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                    Visibility(
                      visible: _newTask == false,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 10.0),
                        child: Text(
                          Translator.text('Common', 'Title') + ': ' + _textEditingControllerTitle.text,
                        ),
                      ),
                    ),
                    Wrap(
                      spacing: 5,
                      runSpacing: 10,
                      children: [
                        Form(
                          child: Container(
                            width: 350,
                            padding: const EdgeInsets.all(10.0),
                            child: Column(
                              children: [
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerTitle,
                                    decoration: InputDecoration(
                                      labelText: Translator.text('Common', 'Title'),
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerDescription,
                                    maxLines: 5,
                                    maxLength: 255,
                                    decoration: InputDecoration(
                                      labelText: Translator.text('Common', 'Description'),
                                      border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(0.0))),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                        LayoutBuilder(
                            builder: (BuildContext context, BoxConstraints constraints) {
                              double w = constraints.maxWidth < 535 ? 350 : 230;
                              return ConstrainedBox(
                                constraints: BoxConstraints(maxWidth: w),
                                child: Padding(
                                  padding: EdgeInsets.only(
                                      top: _newTask ? 40.0 : 20.0, right: 10, left: 10
                                  ),
                                  child: _widgetAffiliates,
                                ),
                              );
                            }
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: EdgeInsets.only(top: 15.0, right: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(Translator.text('Common', 'Cancel')),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, left: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newTask ? Translator.text('Common', ButtonID.CREATE) : Translator.text('Common', ButtonID.APPLY)),
                    onPressed: () {
                      if (_newTask) {
                        _createTask(context);
                      }
                      else {
                        _applyChanges(context);
                      }
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _createTask(BuildContext context) {
    if (_textEditingControllerTitle.text.isEmpty) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetTask', 'Please choose a task name!'), true);
      return;
    }

    Task task = new Task();
    task.title = _textEditingControllerTitle.text;
    task.description = _textEditingControllerDescription.text;
    task.users = _widgetAffiliates.getUserIDs();
    task.teams = _widgetAffiliates.getTeamIDs();

    if ((task.users.length < 1) && (task.teams.length < 1)) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetTask', 'Please choose at least one team or user affiliation.'), true);
      return;
    }

    _serviceTask
        .createTask(task)
        .then((id) {
          DialogModal(context).show(
              Translator.text('WidgetTask', 'New Task'),
              Translator.text('WidgetTask', 'New task was successfully created.'), false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = Translator.text('WidgetTask', 'Could not create new task!\nReason: A task with given title already exists.');
          }
          else {
            text = Translator.text('WidgetTask', 'Could not create new task!\nReason: ') + err.toString();
          }
          DialogModal(context).show(Translator.text('Common', 'Attention'), text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    Task task = new Task();
    task.id = _currentTask.id;
    task.title = _textEditingControllerTitle.text;
    task.description = _textEditingControllerDescription.text;
    task.users = _widgetAffiliates.getUserIDs();
    task.teams = _widgetAffiliates.getTeamIDs();

    if ((task.users.length < 1) && (task.teams.length < 1)) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetTask', 'Please choose at least one team or user affiliation.'), true);
      return;
    }

    _serviceTask
      .editTask(task)
      .then((success) {
          if (success) {
            DialogModal(context).show(
                Translator.text('AppTaskTracker', 'Edit Task'),
                Translator.text('Common', 'All changes successfully applied.'), false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('WidgetTask', 'Could not apply changes! Reason: ') + err.toString(), true);
        }
      );
  }

  void _retrieveTask() {
    if(taskId == 0) {
      print(Translator.text('WidgetTask', 'Internal error, use this widget for an authenticated user'));
      return;
    }

    _serviceTask
        .getTask(taskId)
        .then((task) {
          _currentTask = task;
          _textEditingControllerTitle.text = _currentTask.title;
          _textEditingControllerDescription.text = _currentTask.description;
          _widgetAffiliates.setUserIDs(_currentTask.users);
          _widgetAffiliates.setTeamIDs(_currentTask.teams);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('WidgetTask', 'Could not retrieve task! Reason: ') + err.toString(), true);
        }
    );
  }
}
