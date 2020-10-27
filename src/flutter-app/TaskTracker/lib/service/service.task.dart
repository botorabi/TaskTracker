/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';
import 'dart:io';

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/service/service.common.dart';
import 'package:TaskTracker/service/task.dart';
import 'package:http/http.dart';

class ServiceTask {

  Future<List<Task>> getTasks() async {
    Response response = await get(Config.BASE_URL + '/api/task',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Task.listFromJsonString(response.body);
    }
    else {
      return Future<List<Task>>.error(response.statusCode);
    }
  }

  Future<Task> getTask(int taskId) async {
    Response response = await get(Config.BASE_URL + '/api/task/' + taskId.toString(),
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Task.fromJsonString(response.body);
    }
    else {
      return Future<Task>.error(response.statusCode);
    }
  }

  Future<bool> editTask(Task task) async {
    Response response = await put(Config.BASE_URL + '/api/task/edit',
                                  headers: ServiceCommon.HTTP_HEADERS_REST,
                                  body: jsonEncode(task));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<int> createTask(Task task) async {
    Response response = await post(Config.BASE_URL + '/api/task/create',
                                   headers: ServiceCommon.HTTP_HEADERS_REST,
                                   body: jsonEncode(task));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> deleteTask(int id) async {
    Response response = await delete(Config.BASE_URL + '/api/task/delete/' + id.toString(),
                                     headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }
}
