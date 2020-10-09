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
import 'package:TaskTracker/service/progress.dart';
import 'package:TaskTracker/service/progress.paged.dart';
import 'package:TaskTracker/service/service.common.dart';
import 'package:http/http.dart';

class ServiceProgress {

  Future<int> createProgress(Progress progress) async {
    Response response = await post(Config.baseURL + '/api/progress/create',
        headers: ServiceCommon.HTTP_HEADERS_REST,
        body: jsonEncode(progress));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> editProgress(Progress progress) async {
    Response response = await put(Config.baseURL + '/api/progress/edit',
        headers: ServiceCommon.HTTP_HEADERS_REST,
        body: jsonEncode(progress));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<List<Progress>> getUserProgress() async {
    Response response = await get(Config.baseURL + '/api/progress',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Progress.listFromJsonString(response.body);
    }
    else {
      return Future<List<Progress>>.error(response.statusCode);
    }
  }

  Future<List<Progress>> getTeamProgress(int teamId) async {
    Response response = await get(Config.baseURL + '/api/progress/team/' + teamId.toString(),
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Progress.listFromJsonString(response.body);
    }
    else {
      return Future<List<Progress>>.error(response.statusCode);
    }
  }

  Future<List<Progress>> getAllProgress() async {
    Response response = await get(Config.baseURL + '/api/progress/all',
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Progress.listFromJsonString(response.body);
    }
    else {
      return Future<List<Progress>>.error(response.statusCode);
    }
  }

  Future<ProgressPaged> getPagedProgress(int page, int size) async {
    Response response = await get(Config.baseURL + '/api/progress/paged/' + page.toString() + '/' + size.toString(),
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return ProgressPaged.fromJsonString(response.body);
    }
    else {
      return Future<ProgressPaged>.error(response.statusCode);
    }
  }

  Future<Progress> getProgress(int progressId) async {
    Response response = await get(Config.baseURL + '/api/progress/' + progressId.toString(),
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return Progress.fromJsonString(response.body);
    }
    else {
      return Future<Progress>.error(response.statusCode);
    }
  }

  Future<bool> deleteProgress(int id) async {
    Response response = await delete(Config.baseURL + '/api/progress/delete/' + id.toString(),
        headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }
}
