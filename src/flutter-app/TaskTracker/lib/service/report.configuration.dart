/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

import 'package:TaskTracker/common/utf8.utils.dart';

class ReportMailConfiguration {

  int id;
  String name = '';
  String mailSenderName = '';
  String mailSubject;
  String mailText;
  List<int> reportingTeams = [];
  List<int> masterRecipients = [];
  bool reportToTeamLeads;
  bool reportToTeamMembers;
  String reportPeriod = "";
  String reportWeekDay = "";
  int reportHour = 0;
  int reportMinute = 0;
  String reportTitle;
  String reportSubTitle;

  ReportMailConfiguration();

  factory ReportMailConfiguration.fromMap(final Map<String, dynamic> fields) {
    ReportMailConfiguration configuration = ReportMailConfiguration();
    configuration.id = fields['id'];
    configuration.name = Utf8Utils.fromUtf8(fields['name']);
    configuration.mailSenderName = Utf8Utils.fromUtf8(fields['mailSenderName']);
    configuration.mailSubject = Utf8Utils.fromUtf8(fields['mailSubject']);
    if (fields['mailText'] != null) {
      configuration.mailText = Utf8Utils.fromUtf8(fields['mailText']);
    }
    if (fields['reportingTeams'] != null) {
      configuration.reportingTeams = List.from(fields['reportingTeams']);
    }
    if (fields.containsKey('masterRecipients')) {
      configuration.masterRecipients = List.from(fields['masterRecipients']);
    }
    configuration.reportToTeamLeads = fields['reportToTeamLeads'];
    configuration.reportToTeamMembers = fields['reportToTeamMembers'];
    configuration.reportPeriod = fields['reportPeriod'];
    configuration.reportWeekDay = fields['reportWeekDay'];
    configuration.reportHour = fields['reportHour'];
    configuration.reportMinute = fields['reportMinute'];
    configuration.reportTitle = Utf8Utils.fromUtf8(fields['reportTitle']);
    configuration.reportSubTitle = Utf8Utils.fromUtf8(fields['reportSubTitle']);

    return configuration;
  }

  factory ReportMailConfiguration.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return ReportMailConfiguration.fromMap(fields);
  }

  static List<ReportMailConfiguration> listFromJsonString(final String jsonString) {
    List<ReportMailConfiguration> configurationList = List<ReportMailConfiguration>();
    dynamic configurations = jsonDecode(jsonString);
    configurations.forEach((element) {
      configurationList.add(ReportMailConfiguration.fromMap(element));
    });
    return configurationList;
  }

  Map toJson() {
    return {
      'id' : id,
      'name' : name,
      'mailSenderName' : mailSenderName,
      'mailSubject' : mailSubject,
      'mailText' : mailText,
      'reportingTeams' : reportingTeams?.toList(),
      'masterRecipients' : masterRecipients?.toList(),
      'reportToTeamLeads' : reportToTeamLeads,
      'reportToTeamMembers' : reportToTeamMembers,
      'reportPeriod' : reportPeriod,
      'reportWeekDay' : reportWeekDay,
      'reportHour' : reportHour,
      'reportMinute' : reportMinute,
      'reportTitle' : reportTitle,
      'reportSubTitle' : reportSubTitle,
    };
  }
}
