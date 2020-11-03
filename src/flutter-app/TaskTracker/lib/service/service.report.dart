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
import 'dart:typed_data';

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/service/service.common.dart';
import 'package:http/http.dart';

class ServiceReport {

  Future<bool> createReportDocument(List<int> teamIDs, DateTime fromDate, DateTime toDate,
                                    String title, String subTitle, String fileName) async {

    String deltaTime = '/' + (fromDate.millisecondsSinceEpoch / (24*3600*1000)).floor().toString() +
                       '/' + (toDate.millisecondsSinceEpoch / (24*3600*1000)).floor().toString();

    String teamIDsAsString = teamIDs.join(', ');
    Response response = await get(Config.BASE_URL + '/api/report/team/' + teamIDsAsString +
                                   deltaTime + '/' + title + '/' + subTitle,
                                  headers: ServiceCommon.HTTP_HEADERS_REST_PDF);

    if (response.statusCode == HttpStatus.ok) {
      _writeFile(fileName, response.bodyBytes);
      return true;
    }
    else {
      return Future<bool>.error(response.statusCode);
    }
  }

  void _writeFile(String fileName, Uint8List data) {
    final blob = html.Blob([data]);
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
