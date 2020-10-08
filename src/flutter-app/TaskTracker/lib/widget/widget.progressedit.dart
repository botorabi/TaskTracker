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
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.calendarweek.dart';
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

  WidgetCalendarWeek _widgetCalendarWeek = WidgetCalendarWeek(title: Translator.text('Common', 'Calendar Week'),);

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
                        Translator.text('WidgetProgressEdit', 'Edit Progress Entry'),
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                    Column(
                      children: [
                        Padding(
                          padding: EdgeInsets.only(top: 40.0, right: 20, left: 20, bottom: 20),
                          child:
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Padding(
                                padding: EdgeInsets.only(top: 20.0, right: 10, left: 10),
                                child:
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(Translator.text('Common', 'Task')),
                                    _userTaskDropdownButton,
                                    ],
                                ),
                              ),
                              Padding(
                                padding: EdgeInsets.only(top: 20.0, right: 10, left: 30),
                                child:
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    _widgetCalendarWeek,
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),

                        Form(
                          child: Container(
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
                                    controller: _textEditingControllerText,
                                    textAlignVertical: TextAlignVertical.top,
                                    expands: false,
                                    maxLines: 10,
                                    maxLength: 10 * 1024,
                                    showCursor: true,
                                    decoration: InputDecoration(
                                      labelText: Translator.text('WidgetProgressEdit', 'Your Progress Description'),
                                      hintText: Translator.text('WidgetProgressEdit', '\n- Done great things on this\n- Resolved problems on that\n- ...'),
                                      border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(0.0))),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
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
                    child: Text(Translator.text('Common', ButtonID.CANCEL)),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, left: 10.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(Translator.text('Common', _newProgress ? ButtonID.CREATE : ButtonID.APPLY)),
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
    if (_userTaskDropdownSelection == 0) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, choose a task!'), true);
      return;
    }
    if (_textEditingControllerTitle.text.isEmpty) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, enter a progress title!'), true);
      return;
    }
    if (_textEditingControllerText.text.isEmpty) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, enter a progress description!'), true);
      return;
    }

    Progress progress = new Progress();
    progress.title = _textEditingControllerTitle.text;
    progress.text = _textEditingControllerText.text;
    progress.task = _userTaskDropdownSelection;
    progress.reportWeek = _widgetCalendarWeek.getWeek();
    progress.reportYear = _widgetCalendarWeek.getYear();

    //TODO    progress.tags = _widgetTags.getTags();

    _serviceProgress
        .createProgress(progress)
        .then((id) {
          DialogModal(context).show(
              Translator.text('WidgetProgressEdit', 'New Progress'),
              Translator.text('WidgetProgressEdit', 'New progress entry was successfully created.'), false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = Translator.text('WidgetProgressEdit', 'Could not create new progress entry!\nPlease choose a task and proper calendar week.\n'
              'A maximal calendar week distance of 4 is allowed.');
          }
          else {
            text = Translator.text('WidgetProgressEdit', 'Could not create new progress entry!\nReason: ') + err.toString();
          }
          DialogModal(context).show(Translator.text('Common', 'Attention'), text, true);
        }
    );
  }

  void _applyChanges(BuildContext context) {
    if (_userTaskDropdownSelection == 0) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, choose a task!'), true);
      return;
    }
    if (_textEditingControllerTitle.text.isEmpty) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, enter a progress title!'), true);
      return;
    }
    if (_textEditingControllerText.text.isEmpty) {
      DialogModal(context).show(Translator.text('Common', 'Attention'), Translator.text('WidgetProgressEdit', 'Please, enter a text!'), true);
      return;
    }

    Progress progress = new Progress();
    progress.id = _currentProgress.id;
    progress.title = _textEditingControllerTitle.text;
    progress.text = _textEditingControllerText.text;
    progress.task = _userTaskDropdownSelection;
    progress.reportWeek = _widgetCalendarWeek.getWeek();
    progress.reportYear = _widgetCalendarWeek.getYear();

    //TODO progress.tags = _widgetTags.getTags();

    _serviceProgress
      .editProgress(progress)
      .then((success) {
          if (success) {
            DialogModal(context).show(Translator.text('WidgetProgressEdit', 'Edit Progress'),
                Translator.text('WidgetProgressEdit', 'All changes successfully applied.'), false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          String text = Translator.text('WidgetProgressEdit', 'Could not apply changes to progress entry!\nPlease choose a task and proper calendar week.\n'
              'A maximal calendar week distance of 4 is allowed.');

          DialogModal(context).show(Translator.text('Common', 'Attention'), text, true);
        }
      );
  }

  void _retrieveUserTasksAndSelect([int selectTaskId = 0]) {
    int ownerId = 0;
    if (!_newProgress) {
      ownerId = (Config.authStatus.isAdmin() || Config.authStatus.isTeamLead()) ?
                _currentProgress.ownerId : Config.authStatus.userId;
    }
    else {
      ownerId = Config.authStatus.userId;
    }

    _serviceUser
        .getUserTasks(ownerId)
        .then((tasks) {
          if (tasks.isEmpty == false) {
            _userTaskDropdownItems = tasks.map((task) => DropdownMenuItem<int>(value: task.id, child: Text(task.title))).toList();
          }
          else {
            _userTaskDropdownItems = List<DropdownMenuItem<int>>();
          }
          _userTaskDropdownItems.insert(0, DropdownMenuItem<int>(value: 0, child: Text(Translator.text('WidgetProgressEdit', '<Choose a Task>'))));
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
          _widgetCalendarWeek.set(_currentProgress.reportYear?? 0, _currentProgress.reportWeek?? 0);

          //TODO  _widgetTags.setTags(_currentProgress.tags);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('WidgetProgressEdit', 'Could not retrieve progress entry! Reason: ') + err.toString(), true);
        }
    );
  }

  void _updateTaskChooser(int taskId) {
    if (taskId != 0) {
      _userTaskDropdownSelection = _checkTaskId(taskId);
    }
    else {
      _userTaskDropdownSelection = taskId;
    }

    _userTaskDropdownButton = DropdownButton(
      value: _userTaskDropdownSelection,
      items: _userTaskDropdownItems,
      onChanged: (newValue) => _updateTaskChooser(newValue),
    );
    setState(() {});
  }

  int _checkTaskId(int taskId) {
    for (int i = 0; i < _userTaskDropdownItems.length; i++) {
      if (_userTaskDropdownItems[i].value == taskId) {
        return taskId;
      }
    }
    DialogModal(context).show(
        Translator.text('Common', 'Attention'),
        Translator.text('WidgetProgressEdit', 'Progress task not longer exists!'), true);
    return 0;
  }
}
