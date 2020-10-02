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
import 'package:TaskTracker/service/team.dart';
import 'package:http/http.dart';

class ServiceTeam {

  Future<List<Team>> getTeams() async {
    Response response = await get(Config.baseURL + '/api/team',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Team.listFromJsonString(response.body);
    }
    else {
      return Future<List<Team>>.error(response.statusCode);
    }
  }

  Future<Team> getTeam(int teamId) async {
    Response response = await get(Config.baseURL + '/api/team/' + teamId.toString(),
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Team.fromJsonString(response.body);
    }
    else {
      return Future<Team>.error(response.statusCode);
    }
  }

  Future<bool> editTeam(Team team) async {
    Response response = await put(Config.baseURL + '/api/team/edit',
                                  headers: ServiceCommon.HTTP_HEADERS_REST,
                                  body: jsonEncode(team));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<int> createTeam(Team team) async {
    Response response = await post(Config.baseURL + '/api/team/create',
                                   headers: ServiceCommon.HTTP_HEADERS_REST,
                                   body: jsonEncode(team));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> deleteTeam(int id) async {
    Response response = await delete(Config.baseURL + '/api/team/delete/' + id.toString(),
                                     headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<List<Team>> searchTeam(String value) async {
    Response response = await get(Config.baseURL + '/api/team/search/' + value,
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Team.listFromJsonString(response.body);
    }
    else {
      return Future<List<Team>>.error(response.statusCode);
    }
  }
}
