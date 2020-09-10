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
import 'package:TaskTracker/service/progress.dart';
import 'package:TaskTracker/service/service.progress.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:flutter/material.dart';


class WidgetProgressEdit extends StatefulWidget {
  WidgetProgressEdit({Key key, this.title, this.progressId}) : super(key: key);

  final String title;
  final int    progressId;

  @override
  _WidgetProgressEditState createState() => _WidgetProgressEditState(progressId: progressId);
}

class _WidgetProgressEditState extends State<WidgetProgressEdit> {

  int progressId;

  bool  _newProgress;
  Progress  _currentProgress;
  DropdownButton _userTaskDropdownButton = DropdownButton();
  List<DropdownMenuItem<int>> _userTaskDropdownItems = List();
  int _userTaskDropdownSelection = 0;

  DropdownButton _calendarWeekDropdownButton = DropdownButton();
  List<DropdownMenuItem<int>> _calendarWeekDropdownItems = List();
  int _calendarWeekDropdownSelection = 0;

  final _serviceProgress = ServiceProgress();
  final _serviceUser = ServiceUser();
  final _textEditingControllerTitle = TextEditingController();
  final _textEditingControllerText = TextEditingController();

  _WidgetProgressEditState({this.progressId = 0}) {
    _newProgress = progressId == 0;
  }

  @override
  void initState() {
    super.initState();

    _setupCalendarWeekChooser();

    if (!_newProgress) {
      _retrieveProgress();
    }
    else {
      _retrieveUserTasksAndSelect();
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
        constraints: BoxConstraints(maxWidth: Config.defaultEditorWidth),
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
                        'Edit Progress Entry',
                        style: Theme.of(context).textTheme.headline6,
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
                                      labelText: 'Title',
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: TextFormField(
                                    controller: _textEditingControllerText,
                                    maxLines: 5,
                                    maxLength: 10 * 1024,
                                    showCursor: true,
                                    decoration: InputDecoration(
                                      labelText: 'Text',
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                        LayoutBuilder(
                            builder: (BuildContext context, BoxConstraints constraints) {
                              double w = constraints.maxWidth < 535 ? 350 : 180;
                              return ConstrainedBox(
                                constraints: BoxConstraints(maxWidth: w),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Padding(
                                      padding: EdgeInsets.only(top: 40.0, right: 10, left: 10),
                                      child:
                                        Column(
                                          crossAxisAlignment: CrossAxisAlignment.start,
                                          children: [
                                            Text('Task'),
                                            _userTaskDropdownButton,
                                          ],
                                        ),
                                    ),
                                    Padding(
                                      padding: EdgeInsets.only(top: 20.0, right: 10, left: 10),
                                      child:
                                        Column(
                                          crossAxisAlignment: CrossAxisAlignment.start,
                                          children: [
                                            Text('Calendar Week'),
                                            _calendarWeekDropdownButton,
                                          ],
                                      ),
                                    ),
                                  ],
                                )
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
                    child: Text('Cancel'),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, left: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newProgress ? ButtonID.CREATE : ButtonID.APPLY),
                    onPressed: () {
                      if (_newProgress) {
                        _createProgress(context);
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

  void _createProgress(BuildContext context) {
    if (_textEditingControllerTitle.text.isEmpty) {
      DialogModal(context).show("Attention", "Enter a progress title!", true);
      return;
    }

    Progress progress = new Progress();
    progress.title = _textEditingControllerTitle.text;
    progress.text = _textEditingControllerText.text;
    progress.task = _userTaskDropdownSelection;
    progress.calendarWeek = _calendarWeekDropdownSelection;

//TODO    progress.tags = _widgetTags.getTags();

    _serviceProgress
        .createProgress(progress)
        .then((id) {
          DialogModal(context).show("New Progress", "New progress entry was successfully created.", false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = "Could not create new progress entry!\nPlease choose a task.";
          }
          else {
            text = "Could not create new progress entry!\nReason:" + err.toString();
          }
          DialogModal(context).show("Attention", text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    if (_textEditingControllerTitle.text.isEmpty) {
      DialogModal(context).show("Attention", "Enter a progress title!", true);
      return;
    }

    Progress progress = new Progress();
    progress.id = _currentProgress.id;
    progress.title = _textEditingControllerTitle.text;
    progress.text = _textEditingControllerText.text;
    progress.task = _userTaskDropdownSelection;
    progress.calendarWeek = _calendarWeekDropdownSelection;

//TODO    progress.tags = _widgetTags.getTags();

    _serviceProgress
      .editProgress(progress)
      .then((success) {
          if (success) {
            DialogModal(context).show("Edit Progress", "All changes successfully applied.", false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not apply changes! Reason:" + err.toString(), true);
        }
      );
  }

  void _retrieveUserTasksAndSelect([int selectTaskId = 0]) {
    _serviceUser
        .getUserTasks(Config.authStatus.userId)
        .then((tasks) {
          _userTaskDropdownItems = tasks.map((task) => DropdownMenuItem<int>(value: task.id, child: Text(task.title))).toList();
          _userTaskDropdownItems.insert(0, DropdownMenuItem<int>(value: 0, child: Text('<Choose a Task>')));
          _updateTaskChooser(selectTaskId);
      });
  }

  void _retrieveProgress() async {
    if(progressId == 0) {
      print('Internal error, use this widget for an authenticated user');
      return;
    }

    _serviceProgress
        .getProgress(progressId)
        .then((progress) {
          _currentProgress = progress;
          _textEditingControllerTitle.text = _currentProgress.title;
          _textEditingControllerText.text = _currentProgress.text;
          _retrieveUserTasksAndSelect(_currentProgress.task);
          _updateCalendarWeekChooser(_currentProgress.calendarWeek?? 0);

//TODO          _widgetTags.setTags(_currentProgress.tags);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not retrieve progress entry! Reason: " + err.toString(), true);
        }
    );
  }

  void _updateTaskChooser(int taskId) {
    _userTaskDropdownSelection = taskId;
    _userTaskDropdownButton = DropdownButton(
      value: _userTaskDropdownSelection,
      items: _userTaskDropdownItems,
      onChanged: (newValue) => _updateTaskChooser(newValue),
    );
    setState(() {});
  }

  void _setupCalendarWeekChooser() {
    for (int i = 1; i < 54; i++) {
      _calendarWeekDropdownItems.add(DropdownMenuItem<int>(value: i, child: Text(i.toString())));
    }
  }

  void _updateCalendarWeekChooser(int calendarWeek) {
    if (calendarWeek == 0) {
      final now = DateTime.now();
      final days = now.difference(DateTime(now.year, 1, 1, 0, 0)).inDays;
      calendarWeek = 1 + ((days - 1) / 7).floor();
    }
    print("Current calendar week: " + calendarWeek.toString());

    _calendarWeekDropdownSelection = calendarWeek;
    _calendarWeekDropdownButton = DropdownButton(
      value: _calendarWeekDropdownSelection,
      items: _calendarWeekDropdownItems,
      onChanged: (newValue) => _updateCalendarWeekChooser(newValue),
    );
    setState(() {});
  }
}
