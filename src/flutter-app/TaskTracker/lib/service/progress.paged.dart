/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

import 'package:TaskTracker/common/utf8.utils.dart';
import 'package:TaskTracker/service/progress.dart';

class ProgressPaged {

  int totalCount;
  int currentPage;
  List<Progress> progressList = [];

  ProgressPaged();

  factory ProgressPaged.fromMap(final Map<String, dynamic> fields) {
    ProgressPaged progressPaged = ProgressPaged();
    progressPaged.totalCount = fields['totalCount'];
    progressPaged.currentPage = fields['currentPage'];
    if (fields.containsKey('progressList')) {
      progressPaged.progressList = List<Progress>();
      fields['progressList'].forEach((progress) {
        progressPaged.progressList.add(Progress.fromMap(progress));
      });
    }
    return progressPaged;
  }

  factory ProgressPaged.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return ProgressPaged.fromMap(fields);
  }
}
