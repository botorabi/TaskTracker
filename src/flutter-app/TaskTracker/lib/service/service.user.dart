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
import 'package:TaskTracker/service/userinfo.dart';
import 'package:http/http.dart';

class ServiceUser {

  static final Map<String, String> _httpHeaders = {
    'Content-type': 'application/json',
    'withCredentials': 'true'
  };

  Future<List<UserInfo>> getUsers() async {
    Response response = await get(Config.baseURL + '/api/user',
                                  headers: _httpHeaders);

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
                                  headers: _httpHeaders);

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
                                  headers: _httpHeaders);

    if (response.statusCode == HttpStatus.ok) {
      return UserInfo.fromJsonString(response.body);
    }
    else {
      return Future<UserInfo>.error(response.statusCode);
    }
  }

  Future<bool> edit(UserInfo userInfo) async {
    Response response = await put(Config.baseURL + '/api/user/edit',
                                  headers: _httpHeaders,
                                  body: jsonEncode(userInfo));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<int> create(UserInfo userInfo) async {
    Response response = await post(Config.baseURL + '/api/user/create',
                                   headers: _httpHeaders,
                                   body: jsonEncode(userInfo));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> deleteUser(int id) async {
    Response response = await delete(Config.baseURL + '/api/user/' + id.toString(),
                                     headers: _httpHeaders);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }
}
