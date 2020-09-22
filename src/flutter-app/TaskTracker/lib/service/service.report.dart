/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';
import 'dart:html' as html;
import 'dart:io';

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/service/service.common.dart';
import 'package:http/http.dart';

class ServiceReport {

  Future<bool> createReportText(List<int> teamIDs, DateTime fromDate, DateTime toDate, String fileName) async {
    String deltaTime = '/' + (fromDate.millisecondsSinceEpoch / (24*3600*1000)).floor().toString() +
                       '/' + (toDate.millisecondsSinceEpoch / (24*3600*1000)).floor().toString();

    String teamIDsAsString = teamIDs.join(',');
    Response response = await get(Config.baseURL + '/api/report/team/' + teamIDsAsString + deltaTime,
                                  headers: ServiceCommon.HTTP_HEADERS_REST);

    if (response.statusCode == HttpStatus.ok) {
      _writeTextFile(fileName, response.body);
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  void _writeTextFile(String fileName, String data) {
    final bytes = utf8.encode(data);
    final blob = html.Blob([bytes]);
    final url = html.Url.createObjectUrlFromBlob(blob);
    final anchor = html.document.createElement('a') as html.AnchorElement
      ..href = url
      ..style.display = 'none'
      ..download = fileName;

    html.document.body.children.add(anchor);
    anchor.click();
    html.document.body.children.remove(anchor);
    html.Url.revokeObjectUrl(url);
  }
}
