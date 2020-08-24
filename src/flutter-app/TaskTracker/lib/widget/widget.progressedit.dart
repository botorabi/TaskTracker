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
  final _serviceProgress = ServiceProgress();
  final _textEditingControllerTitle = TextEditingController();
  final _textEditingControllerText = TextEditingController();
  final _widgetChooseTask = Text('TODO choose task');//WidgetChooseTask();

  _WidgetProgressEditState({this.progressId = 0}) {
    _newProgress = progressId == 0;
  }

  @override
  void initState() {
    super.initState();
    if (!_newProgress) {
      _retrieveProgress();
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
                                child: Padding(
                                  padding: EdgeInsets.only(
                                      top: _newProgress ? 40.0 : 20.0, right: 10, left: 10
                                  ),
                                  child: _widgetChooseTask,
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
    progress.title = _textEditingControllerText.text;
    progress.text = _textEditingControllerText.text;
//TODO    progress.task = _widgetChooseTask;
//TODO    progress.tags = _widgetTags;

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
    Progress progress = new Progress();
    progress.id = _currentProgress.id;
    progress.title = _textEditingControllerText.text;
    progress.text = _textEditingControllerText.text;

//TODO    progress.task = _widgetChooseTask.getTask();
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

  void _retrieveProgress() {
    if(progressId == 0) {
      print('Internal error, use this widget for an authenticated user');
      return;
    }

    _serviceProgress
        .getProgress(progressId)
        .then((progress) {
          _currentProgress = progress;
          _textEditingControllerText.text = _currentProgress.title;
          _textEditingControllerText.text = _currentProgress.text;

//TODO          _widgetChooseTask.setTask(_currentProgress.task);
//TODO          _widgetTags.setTags(_currentProgress.tags);

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show("Attention", "Could not retrieve progress entry! Reason: " + err.toString(), true);
        }
    );
  }
}
