/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:async';

import 'package:TaskTracker/sessiontimeout.dart';
import 'package:flutter/material.dart';


class WidgetSessionTimeout extends StatefulWidget {

  final TextStyle textStyle;

  WidgetSessionTimeout({this.textStyle});

  @override
  _WidgetSessionTimeoutState createState() => _WidgetSessionTimeoutState();
}

class _WidgetSessionTimeoutState extends State<WidgetSessionTimeout> {

  static const int UPDATE_PERIOD = 1;

  String _timeoutText = '';
  Timer  _updateTimer;

  @override
  void dispose() {
    super.dispose();
    _updateTimer?.cancel();
  }
  
  @override
  void initState() {
    super.initState();
    _setupRefreshTimer();
  }

  @override
  Widget build(BuildContext context) {
    return Text(_timeoutText, style: widget.textStyle);
  }

  void _setupRefreshTimer() {
    _updateTimer?.cancel();
    setState(() {
      _timeoutText = SessionTimeoutHandler.timeLeftFormattedString();
    });
    _updateTimer = Timer(Duration(seconds: UPDATE_PERIOD), _setupRefreshTimer);
  }
}

