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
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:http/http.dart';

class ServiceUser {

  Future<List<UserInfo>> getUsers() async {
    Response response = await get(Config.baseURL + '/api/user',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      List<UserInfo> users = UserInfo.listFromJsonString(response.body);
      return users;
    }
    else {
      return Future<List<UserInfo>>.error(response.statusCode);
    }
  }

  Future<List<String>> getAvailableRoles() async {
    Response response = await get(Config.baseURL + '/api/user/availableroles',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      List<String> roles = List<String>();
      dynamic users = jsonDecode(response.body);
      users.forEach((element) {
        roles.add(element);
      });
      return roles;
    }
    else {
      return Future<List<String>>.error(response.statusCode);
    }
  }

  Future<UserInfo> getUser(int userId) async {
    Response response = await get(Config.baseURL + '/api/user/' + userId.toString(),
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return UserInfo.fromJsonString(response.body);
    }
    else {
      return Future<UserInfo>.error(response.statusCode);
    }
  }

  Future<bool> editUser(UserInfo userInfo) async {
    Response response = await put(Config.baseURL + '/api/user/edit',
                                  headers: ServiceCommon.HTTP_HEADERS_REST,
                                  body: jsonEncode(userInfo));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<int> createUser(UserInfo userInfo) async {
    Response response = await post(Config.baseURL + '/api/user/create',
                                   headers: ServiceCommon.HTTP_HEADERS_REST,
                                   body: jsonEncode(userInfo));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> deleteUser(int id) async {
    Response response = await delete(Config.baseURL + '/api/user/delete/' + id.toString(),
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<List<UserInfo>> searchUser(String value) async {
    Response response = await get(Config.baseURL + '/api/user/search/' + value,
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return UserInfo.listFromJsonString(response.body);
    }
    else {
      return Future<List<UserInfo>>.error(response.statusCode);
    }
  }

  Future<List<Task>> getUserTasks(int userId) async {
    Response response = await get(Config.baseURL + '/api/user/tasks/' + userId.toString(),
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Task.listFromJsonString(response.body);
    }
    else {
      return Future<List<Task>>.error(response.statusCode);
    }
  }

  Future<List<Team>> getUserTeams() async {
    Response response = await get(Config.baseURL + '/api/user/teams',
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Team.listFromJsonString(response.body);
    }
    else {
      return Future<List<Team>>.error(response.statusCode);
    }
  }
}
