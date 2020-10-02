/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/calendar.utils.dart';
import 'package:flutter/material.dart';


class WidgetCalendarWeek extends StatefulWidget {
  WidgetCalendarWeek({Key key, this.title}) : super(key: key);

  final String title;
  final _WidgetCalendarWeekState _widgetCalendarWeekState = _WidgetCalendarWeekState();

  @override
  _WidgetCalendarWeekState createState() => _widgetCalendarWeekState;

  void set(int year, int week) {
    _widgetCalendarWeekState._updateCalendarYearChooser(year);
    _widgetCalendarWeekState._updateCalendarWeekChooser(week);
  }

  int getWeek() => _widgetCalendarWeekState._calendarWeekDropdownSelection;

  int getYear() => _widgetCalendarWeekState._calendarYearDropdownSelection;
}

class _WidgetCalendarWeekState extends State<WidgetCalendarWeek> {

  DropdownButton _calendarWeekDropdownButton = DropdownButton();
  List<DropdownMenuItem<int>> _calendarWeekDropdownItems = List();
  int _calendarWeekDropdownSelection = 0;

  DropdownButton _calendarYearDropdownButton = DropdownButton();
  List<DropdownMenuItem<int>> _calendarYearDropdownItems = List();
  int _calendarYearDropdownSelection = 0;

  @override
  void initState() {
    super.initState();

    _setupCalendarWeekChooser();
    _setupCalendarYearChooser();
    _updateCalendarWeekChooser(0);
    _updateCalendarYearChooser(0);
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(widget.title),
        Row(
          children: [
            _calendarYearDropdownButton,
            SizedBox(width: 10.0),
            _calendarWeekDropdownButton,
          ],
        )
      ],
    );
  }

  void _setupCalendarWeekChooser() {
    for (int i = 1; i < 54; i++) {
      _calendarWeekDropdownItems.add(DropdownMenuItem<int>(value: i, child: Text(i.toString())));
    }
  }

  void _setupCalendarYearChooser() {
    int currentYear = CalendarUtils.getCurrentCalendarYear();
    for (int i = currentYear - 10; i < currentYear + 10; i++) {
      _calendarYearDropdownItems.add(DropdownMenuItem<int>(value: i, child: Text(i.toString())));
    }
  }

  void _updateCalendarWeekChooser(int calendarWeek) {
    if (calendarWeek == 0) {
      calendarWeek = CalendarUtils.getCurrentCalendarWeek();
    }

    _calendarWeekDropdownSelection = calendarWeek;
    _calendarWeekDropdownButton = DropdownButton(
      value: _calendarWeekDropdownSelection,
      items: _calendarWeekDropdownItems,
      onChanged: (newValue) => _updateCalendarWeekChooser(newValue),
    );
    setState(() {});
  }

  void _updateCalendarYearChooser(int calendarYear) {
    if (calendarYear == 0) {
      calendarYear = CalendarUtils.getCurrentCalendarYear();
    }

    _calendarYearDropdownSelection = calendarYear;
    _calendarYearDropdownButton = DropdownButton(
      value: _calendarYearDropdownSelection,
      items: _calendarYearDropdownItems,
      onChanged: (newValue) => _updateCalendarYearChooser(newValue),
    );
    setState(() {});
  }
}
