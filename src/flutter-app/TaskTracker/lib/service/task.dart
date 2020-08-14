/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class Task {

  int id;
  String title = '';
  String description = '';
  DateTime dateCreation;
  DateTime dateClosed;
  List<int> users = [];
  List<int> teams = [];

  Task();

  factory Task.fromMap(final Map<String, dynamic> fields) {
    Task task = Task();
    task.id = fields['id'];
    task.title = fields['title'];
    task.description = fields['description'];
    if (fields['dateCreation'] != null) {
      task.dateCreation = DateTime.parse(fields['dateCreation'].toString());
    }
    if (fields['dateClosed'] != null) {
      task.dateClosed = DateTime.parse(fields['dateClosed'].toString());
    }
    if (fields.containsKey('users')) {
      task.users = List.from(fields['users']);
    }
    if (fields.containsKey('teams')) {
      task.teams = List.from(fields['teams']);
    }
    return task;
  }

  factory Task.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return Task.fromMap(fields);
  }

  static List<Task> listFromJsonString(final String jsonString) {
    List<Task> taskList = List<Task>();
    dynamic tasks = jsonDecode(jsonString);
    tasks.forEach((element) {
      taskList.add(Task.fromMap(element));
    });
    return taskList;
  }

  Map toJson() {
    return {
      'id' : id,
      'title' : title,
      'description' : description,
      'users' : users?.toList(),
      'teams' : teams?.toList(),
      'dateClosed' : dateClosed?.toString(),
    };
  }
}
