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
import 'package:TaskTracker/service/report.configuration.dart';
import 'package:TaskTracker/service/service.common.dart';
import 'package:TaskTracker/service/task.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:http/http.dart';

class ServiceReportConfiguration {

  Future<List<ReportMailConfiguration>> getConfigurations() async {
    Response response = await get(Config.baseURL + '/api/report/generator-configuration',
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      List<ReportMailConfiguration> configurations = ReportMailConfiguration.listFromJsonString(response.body);
      return configurations;
    }
    else {
      return Future<List<ReportMailConfiguration>>.error(response.statusCode);
    }
  }

  Future<ReportMailConfiguration> getConfiguration(int configurationId) async {
    Response response = await get(Config.baseURL + '/api/report/generator-configuration/' + configurationId.toString(),
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return ReportMailConfiguration.fromJsonString(response.body);
    }
    else {
      return Future<ReportMailConfiguration>.error(response.statusCode);
    }
  }

  Future<int> createConfiguration(ReportMailConfiguration configuration) async {
    Response response = await post(Config.baseURL + '/api/report/generator-configuration/create',
        headers: ServiceCommon.HTTP_HEADERS_REST,
        body: jsonEncode(configuration));

    if (response.statusCode == HttpStatus.ok) {
      return int.parse(response.body);
    }
    else {
      return Future<int>.error(response.statusCode);
    }
  }

  Future<bool> editConfiguration(ReportMailConfiguration configuration) async {
    Response response = await put(Config.baseURL + '/api/report/generator-configuration/edit',
                                  headers: ServiceCommon.HTTP_HEADERS_REST,
                                  body: jsonEncode(configuration));

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  Future<bool> deleteConfiguration(int id) async {
    Response response = await delete(Config.baseURL + '/api/report/generator-configuration/delete/' + id.toString(),
                                     headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }
}
