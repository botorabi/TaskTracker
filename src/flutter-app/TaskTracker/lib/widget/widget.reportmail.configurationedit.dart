/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:io';

import 'package:TaskTracker/common/button.id.dart';
import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/dialog/dialog.modal.dart';
import 'package:TaskTracker/service/report.configuration.dart';
import 'package:TaskTracker/service/service.report.configuration.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.teamchooser.dart';
import 'package:TaskTracker/widget/widget.userchooser.dart';
import 'package:flutter/material.dart';


class WidgetReportConfigurationEdit extends StatefulWidget {
  WidgetReportConfigurationEdit({Key key, this.title, this.configurationId}) : super(key: key);

  final String title;
  final int    configurationId;

  @override
  _WidgetReportConfigurationEditState createState() => _WidgetReportConfigurationEditState(configurationId: configurationId);
}

class _WidgetReportConfigurationEditState extends State<WidgetReportConfigurationEdit> {

  int configurationId;

  bool _newConfiguration;
  ReportMailConfiguration _currentReportConfiguration;
  final _serviceReportConfiguration = ServiceReportConfiguration();
  final _textEditingControllerConfigName = TextEditingController();
  final _textEditingControllerMailSenderName = TextEditingController();
  final _textEditingControllerMailSubject = TextEditingController();
  final _textEditingControllerMailText = TextEditingController();
  final _textEditingControllerReportHour = TextEditingController(text:'18');
  final _textEditingControllerReportMinute = TextEditingController(text:'00');
  final _textEditingControllerReportTitle = TextEditingController();
  final _textEditingControllerReportSubTitle = TextEditingController();
  bool _reportToTeamLeads = true;
  bool _reportToTeamMembers = false;
  String _reportPeriod = "PERIOD_WEEKLY";
  String _reportWeekDay = "WEEKDAY_FRIDAY";

  WidgetUserChooser _widgetMasterRecipients = WidgetUserChooser(title: Translator.text('WidgetReportMailConfiguration', 'Additional Recipients'));
  WidgetTeamChooser _widgetReportingTeams = WidgetTeamChooser(title: Translator.text('WidgetReportMailConfiguration', 'Reporting Teams'));

  _WidgetReportConfigurationEditState({this.configurationId}) {
    if (configurationId != 0) {
      _newConfiguration = false;
    }
    else {
      _newConfiguration = true;
    }
  }

  @override
  void initState() {
    super.initState();
    if (!_newConfiguration) {
      _retrieveReportConfiguration();
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4.0,
      margin: const EdgeInsets.all(30.0),
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: Config.DEFAULT_EDITOR_WIDTH),
        child: Column(
          children: <Widget>[
            ListView(
              shrinkWrap: true,
              children: <Widget>[
                Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(top: 20.0),
                      child: Text(
                        Translator.text('WidgetReportMailConfiguration', 'Edit Report Configuration'),
                        style: Theme.of(context).textTheme.headline6,
                      ),
                    ),
                  ],
                ),
                Wrap(
                  spacing: 5,
                  runSpacing: 10,
                  children: [
                    Form(
                      child: Container(
                        width: 350,
                        padding: const EdgeInsets.all(20.0),
                        child: Column(
                          children: [
                            Visibility(
                              visible: _newConfiguration == true,
                              child: Padding(
                                padding: EdgeInsets.all(10.0),
                                child: TextFormField(
                                  controller: _textEditingControllerConfigName,
                                  autofocus: true,
                                  decoration: InputDecoration(
                                    labelText: Translator.text('WidgetReportMailConfiguration', 'Configuration Name'),
                                  ),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerMailSenderName,
                                decoration: InputDecoration(
                                  labelText: Translator.text('WidgetReportMailConfiguration', 'Mail Sender'),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerMailSubject,
                                decoration: InputDecoration(
                                  labelText: Translator.text('WidgetReportMailConfiguration', 'Mail Subject'),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerMailText,
                                textAlignVertical: TextAlignVertical.top,
                                expands: false,
                                maxLines: 5,
                                maxLength: 1024,
                                showCursor: true,
                                decoration: InputDecoration(
                                  labelText: Translator.text('WidgetReportMailConfiguration', 'Optional Mail Body Text'),
                                  hintText: Translator.text('WidgetReportMailConfiguration', '\nThis is an automatically generated report mail...'),
                                  border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(0.0))),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerReportTitle,
                                decoration: InputDecoration(
                                  labelText: Translator.text('WidgetReportMailConfiguration', 'Report Title'),
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                controller: _textEditingControllerReportSubTitle,
                                decoration: InputDecoration(
                                  labelText: Translator.text('WidgetReportMailConfiguration', 'Report Sub-Title'),
                                ),
                              ),
                            ),
                            Row(
                              children: [
                                Padding(
                                    padding: EdgeInsets.only(left: 10, right: 20.0),
                                    child:
                                    Text(Translator.text('WidgetReportMailConfiguration', 'Report to Team Leads'),
                                      textAlign: TextAlign.left,
                                    )
                                ),
                                Spacer(),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: Checkbox(
                                    value: _reportToTeamLeads,
                                    onChanged: (value) {
                                      setState(() {
                                        _reportToTeamLeads = value;
                                      });
                                    },
                                  ),
                                ),
                              ],
                            ),
                            Row(
                              children: [
                                Padding(
                                    padding: EdgeInsets.only(left: 10, right: 20.0),
                                    child:
                                    Text(Translator.text('WidgetReportMailConfiguration', 'Report to Team Members'),
                                      textAlign: TextAlign.left,
                                    )
                                ),
                                Spacer(),
                                Padding(
                                  padding: EdgeInsets.all(10.0),
                                  child: Checkbox(
                                    value: _reportToTeamMembers,
                                    onChanged: (value) {
                                      setState(() {
                                        _reportToTeamMembers = value;
                                      });
                                    },
                                  ),
                                ),
                              ],
                            ),
                            Row(
                              children: [
                                Padding(
                                    padding: EdgeInsets.only(left: 10),
                                    child:
                                    Text(Translator.text('WidgetReport', 'Report Period'),
                                      textAlign: TextAlign.left,
                                    )
                                ),
                                Spacer(),
                                SizedBox(
                                  width: 120,
                                  child:
                                  DropdownButton<String>(
                                    items: [
                                      DropdownMenuItem<String>(
                                        child: Text(Translator.text('Common', 'Weekly')),
                                        value: 'PERIOD_WEEKLY',
                                      ),
                                      DropdownMenuItem<String>(
                                        child: Text(Translator.text('Common', 'Monthly')),
                                        value: 'PERIOD_MONTHLY',
                                      ),
                                    ],
                                    onChanged: (String value) {
                                      setState(() {
                                        _reportPeriod = value;
                                      });
                                    },
                                    //hint: Text(Translator.text('WidgetReportMailConfiguration', 'Choose Period')),
                                    value: _reportPeriod,
                                  ),
                                ),
                             ],
                            ),
                            SizedBox(height: 10),
                            Row(
                              children: [
                                Padding(
                                    padding: EdgeInsets.only(left: 10),
                                    child:
                                    Text(Translator.text('WidgetReportMailConfiguration', 'Report Week Day'),
                                      textAlign: TextAlign.left,
                                    )
                                ),
                                Spacer(),
                                SizedBox(
                                  width: 120,
                                  child:
                                    DropdownButton<String>(
                                      isDense: true,
                                      items: [
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Monday')),
                                          value: 'WEEKDAY_MONDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Tuesday')),
                                          value: 'WEEKDAY_TUESDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Wednesday')),
                                          value: 'WEEKDAY_WEDNESDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Thursday')),
                                          value: 'WEEKDAY_THURSDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Friday')),
                                          value: 'WEEKDAY_FRIDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Saturday')),
                                          value: 'WEEKDAY_SATURDAY',
                                        ),
                                        DropdownMenuItem<String>(
                                          child: Text(Translator.text('Common', 'Sunday')),
                                          value: 'WEEKDAY_SUNDAY',
                                        ),
                                      ],
                                      onChanged: (String value) {
                                        setState(() {
                                          _reportWeekDay = value;
                                        });
                                      },
                                      //hint: Text(Translator.text('WidgetReportMailConfiguration', 'Choose Week Day')),
                                      value: _reportWeekDay,
                                    ),
                                ),
                              ],
                            ),
                            SizedBox(height: 10),
                            Row(
                              children: [
                                Padding(
                                    padding: EdgeInsets.only(left: 10),
                                    child:
                                    Text(Translator.text('WidgetReportMailConfiguration', 'Report Time'),
                                      textAlign: TextAlign.left,
                                    )
                                ),
                                Spacer(),
                                Row(
                                  children: [
                                    Padding(
                                      padding: EdgeInsets.all(10.0),
                                      child: SizedBox(
                                        width: 20.0,
                                        child: TextFormField(
                                          controller: _textEditingControllerReportHour,
                                        ),
                                      )
                                    ),
                                    Text(':'),
                                    Padding(
                                      padding: EdgeInsets.all(10.0),
                                      child: SizedBox(
                                        width: 20.0,
                                        child: TextFormField(
                                          controller: _textEditingControllerReportMinute,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                    LayoutBuilder(
                        builder: (BuildContext context, BoxConstraints constraints) {
                          double topPadding = constraints.maxWidth < 535 ? 0.0 : 50.0;
                          double w = constraints.maxWidth < 535 ? 350 : 220;
                          return ConstrainedBox(
                            constraints: BoxConstraints(maxWidth: w),
                            child:
                            Column(
                              children: [
                                Padding(
                                  padding: EdgeInsets.only(left: 5, top: topPadding),
                                  child: _widgetReportingTeams,
                                ),
                                Padding(
                                  padding: EdgeInsets.only(left: 5, top: 5),
                                  child: _widgetMasterRecipients,
                                ),
                            ]
                          ),
                        );
                      }
                    ),
                  ],
                ),
              ],
            ),

            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: EdgeInsets.only(top: 10.0, right: 15.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(Translator.text('Common', 'Cancel')),
                    onPressed: () => { Navigator.of(context).pop(ButtonID.CANCEL) },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 10.0, right: 15.0, bottom: 10.0),
                  child: RaisedButton(
                    child: Text(_newConfiguration ? Translator.text('Common', ButtonID.CREATE) : Translator.text('Common', ButtonID.APPLY)),
                    onPressed: () {
                      if (_newConfiguration) {
                        _createReportConfiguration(context);
                      }
                      else {
                        _applyChanges(context);
                      }
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _createReportConfiguration(BuildContext context) {
    if (!validateInput()) {
      return;
    }

    final reportConfiguration = _assembleConfiguration();
    if (reportConfiguration.reportingTeams.length < 1) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReportMailConfiguration', 'Please choose at least one team.'), true);
      return;
    }

    _serviceReportConfiguration
        .createConfiguration(reportConfiguration)
        .then((id) {
          DialogModal(context).show(
              Translator.text('WidgetReportMailConfiguration', 'New Report Configuration'),
              Translator.text('WidgetReportMailConfiguration', 'New report configuration was successfully created.'), false)
              .then((value) => Navigator.of(context).pop(ButtonID.OK));
        },
        onError: (err) {
          String text;
          if (err == HttpStatus.notAcceptable) {
            text = Translator.text('WidgetReportMailConfiguration', 'Could not create new report configuration!');
          }
          else {
            text = Translator.text('WidgetReportMailConfiguration', 'Could not create new report configuration!\nReason: ') + err.toString();
          }
          DialogModal(context).show(Translator.text('Common', 'Attention'), text, true);
        }
    );
  }

  bool validateInput() {
    if (_textEditingControllerConfigName.text.isEmpty) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReportMailConfiguration', 'Please choose a configuration name!'), true);
      return false;
    }
    if (_textEditingControllerMailSenderName.text.isEmpty) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReportMailConfiguration', 'Please choose a name for mail sender!'), true);
      return false;
    }
    if (_textEditingControllerMailSubject.text.isEmpty) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReportMailConfiguration', 'Please choose a subject for report mail!'), true);
      return false;
    }
    return true;
  }

  void _applyChanges(BuildContext context) {
    if (!validateInput()) {
      return;
    }

    final reportConfiguration = _assembleConfiguration();
    reportConfiguration.id = _currentReportConfiguration.id;
    if (reportConfiguration.reportingTeams.length < 1) {
      DialogModal(context).show(
          Translator.text('Common', 'Attention'),
          Translator.text('WidgetReportMailConfiguration', 'Please choose at least one team.'), true);
      return;
    }

    _serviceReportConfiguration
      .editConfiguration(reportConfiguration)
      .then((success) {
          if (success) {
            DialogModal(context).show(
                Translator.text('WidgetReportMailConfiguration', 'Edit Report Configuration'),
                Translator.text('Common', 'All changes successfully applied.'), false)
            .then((value) => Navigator.of(context).pop());
          }
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('Common', 'Could not apply changes! Reason: ') + err.toString(), true);
        }
      );
  }

  ReportMailConfiguration _assembleConfiguration() {
    final reportConfiguration = ReportMailConfiguration();
    reportConfiguration.name = _textEditingControllerConfigName.text;
    reportConfiguration.mailSenderName = _textEditingControllerMailSenderName.text;
    reportConfiguration.mailSubject = _textEditingControllerMailSubject.text;
    reportConfiguration.mailText = _textEditingControllerMailText.text;
    reportConfiguration.reportToTeamLeads = _reportToTeamLeads;
    reportConfiguration.reportToTeamMembers = _reportToTeamMembers;
    reportConfiguration.masterRecipients = _widgetMasterRecipients.getUserIDs();
    reportConfiguration.reportingTeams = _widgetReportingTeams.getTeamIDs();
    reportConfiguration.reportPeriod = _reportPeriod;
    reportConfiguration.reportWeekDay = _reportWeekDay;
    reportConfiguration.reportHour = int.parse(_textEditingControllerReportHour.text);
    reportConfiguration.reportMinute = int.parse(_textEditingControllerReportMinute.text);
    reportConfiguration.reportTitle = _textEditingControllerReportTitle.text;
    reportConfiguration.reportSubTitle = _textEditingControllerReportSubTitle.text;
    return reportConfiguration;
  }

  void _retrieveReportConfiguration() {
    if(configurationId == 0) {
      print(Translator.text('Common', 'Internal error, use this widget for an authenticated user'));
      return;
    }

    _serviceReportConfiguration
        .getConfiguration(configurationId)
        .then((reportConfiguration) {
          _currentReportConfiguration = reportConfiguration;
          _textEditingControllerConfigName.text = _currentReportConfiguration.name;
          _textEditingControllerMailSenderName.text = _currentReportConfiguration.mailSenderName;
          _textEditingControllerMailSubject.text = _currentReportConfiguration.mailSubject;
          _textEditingControllerMailText.text = _currentReportConfiguration.mailText;

          _textEditingControllerReportTitle.text = _currentReportConfiguration.reportTitle;
          _textEditingControllerReportSubTitle.text = _currentReportConfiguration.reportSubTitle;

          _reportToTeamLeads = _currentReportConfiguration.reportToTeamLeads;
          _reportToTeamMembers = _currentReportConfiguration.reportToTeamMembers;

          _widgetMasterRecipients.setUserIDs(_currentReportConfiguration.masterRecipients);
          _widgetReportingTeams.setTeamIDs(_currentReportConfiguration.reportingTeams);

          _reportPeriod = _currentReportConfiguration.reportPeriod;
          _reportWeekDay = _currentReportConfiguration.reportWeekDay;
          _textEditingControllerReportHour.text = _currentReportConfiguration.reportHour.toString();
          if (_textEditingControllerReportHour.text.length < 2) {
            _textEditingControllerReportHour.text = '0' + _textEditingControllerReportHour.text;
          }
          _textEditingControllerReportMinute.text = _currentReportConfiguration.reportMinute.toString();
          if (_textEditingControllerReportMinute.text.length < 2) {
            _textEditingControllerReportMinute.text = '0' + _textEditingControllerReportMinute.text;
          }

          setState(() {});
        },
        onError: (err) {
          DialogModal(context).show(
              Translator.text('Common', 'Attention'),
              Translator.text('WidgetReportMailConfiguration', 'Could not retrieve report configuration! Reason: ') + err.toString(),
              true);
        }
    );
  }
}
