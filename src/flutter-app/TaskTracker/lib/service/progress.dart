/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

import 'package:TaskTracker/common/utf8.utils.dart';

class Progress {

  int id;
  int ownerId;
  String ownerName = '';
  DateTime dateCreation;
  int reportWeek;
  int reportYear;
  String title = '';
  String text = '';
  List<String> tags = [];
  int task;

  String taskName = '';

  Progress();

  factory Progress.fromMap(final Map<String, dynamic> fields) {
    Progress progress = Progress();
    progress.id = fields['id'];
    progress.ownerId = fields['ownerId'];
    progress.ownerName = Utf8Utils.fromUtf8(fields['ownerName']);
    progress.title = Utf8Utils.fromUtf8(fields['title']);
    progress.text = Utf8Utils.fromUtf8(fields['text']);
    progress.reportWeek = fields['reportWeek'];
    progress.reportYear = fields['reportYear'];

    if (fields['dateCreation'] != null) {
      progress.dateCreation = DateTime.parse(fields['dateCreation'].toString()).toLocal();
    }
    if (fields.containsKey('tags')) {
      progress.tags = List.from(fields['tags']);
    }
    if (fields.containsKey('task')) {
      progress.task = fields['task'];
    }
    return progress;
  }

  factory Progress.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return Progress.fromMap(fields);
  }

  static List<Progress> listFromJsonString(final String jsonString) {
    List<Progress> progressList = List<Progress>();
    dynamic progs = jsonDecode(jsonString);
    progs.forEach((element) {
      progressList.add(Progress.fromMap(element));
    });
    return progressList;
  }

  Map toJson() {
    return {
      'id' : id,
      'text' : text,
      'title' : title,
      'tags' : tags?.toList(),
      'task' : task,
      'reportWeek' : reportWeek,
      'reportYear' : reportYear
    };
  }
}
