/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.circle.dart';
import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/service.report.dart';
import 'package:TaskTracker/service/service.user.dart';
import 'package:TaskTracker/service/team.dart';
import 'package:TaskTracker/service/userinfo.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.calendarweek.dart';
import 'package:TaskTracker/widget/widget.reportradiosort.dart';
import 'package:TaskTracker/widget/widget.reporttable.dart';
import 'package:flutter/material.dart';

import '../config.dart';


class WidgetReport extends StatelessWidget {

  final String title;

  final WidgetCalendarWeek _widgetCalendarWeekFrom = WidgetCalendarWeek(title: Translator.text('WidgetReport', 'From'));

  final WidgetCalendarWeek _widgetCalendarWeekTo = WidgetCalendarWeek(title: Translator.text('WidgetReport', 'To'));

  final ReportTableManager _tableManager;

  final bool isTeam;

  WidgetReport({Key key, this.title = 'Progress Report', this.isTeam = false})
      : _tableManager = isTeam ? TeamReportTableManager() : UserReportTableManager(),
        super(key: key)
  {
  }

  void onSortChangedCallBack(final ReportSortType sortType) {
    _tableManager.getDataProvider().setReportSort(sortType);
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) => SingleChildScrollView(
        child: Card(
          elevation: 5,
          margin: EdgeInsets.all(10.0),
          child:
          Column(
              children: [
                Center(
                  child:
                  Padding(
                    padding: EdgeInsets.only(top: 20.0, bottom: 20),
                    child:
                    Text(title,
                        style: TextStyle(fontSize: 20)
                    ),
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 10.0, right: 20, left: 20),
                  child:
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Card(
                        elevation: 1,
                        child:
                        Padding(
                          padding: const EdgeInsets.all(20.0),
                          child:
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                Translator.text('WidgetReport', 'Progress Period'),
                                textAlign: TextAlign.left,
                                style: TextStyle(fontWeight: FontWeight.w400, fontSize: 18),
                              ),
                              SizedBox(height: 20),
                              Row(
                                children: [
                                  _widgetCalendarWeekFrom,
                                  SizedBox(width: 30),
                                  _widgetCalendarWeekTo,
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(top: 10.0, right: 20, left: 20),
                  child: SizedBox(
                    width: constraints.maxWidth,
                    child: Card(
                      child: Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: ReportRadioSort(sortChangedCallBack: onSortChangedCallBack, isTeam: isTeam),
                      ),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(top: 10.0, right: 20, left: 20),
                  child: SizedBox(
                    width: constraints.maxWidth,
                    child: Visibility (
                      visible: (isTeam || Config.authStatus.isAdmin() || Config.authStatus.isTeamLead()),
                      child: _tableManager,
                    ),
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 10.0, bottom: 20.0, right: 25.0),
                  child: Row(
                    children: [
                      Spacer(),
                      ElevatedButton(
                        child: Text(Translator.text('Common', ButtonID.CREATE)),
                        onPressed: () {
                          _createReport(context);
                        },
                      ),
                    ],
                  ),
                ),
              ]
          ),
        ),
      ),
    );
  }

  void _createReport(BuildContext context) async {
    DateTime fromDate = DateTime(_widgetCalendarWeekFrom.getYear(), 1,
        ((_widgetCalendarWeekFrom.getWeek() - 1) * 7));

    DateTime toDate = DateTime(_widgetCalendarWeekTo.getYear(), 1,
        ((_widgetCalendarWeekTo.getWeek()) * 7));

    await _tableManager.getDataProvider().createReport(context, fromDate, toDate);
  }
}

