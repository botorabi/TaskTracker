/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.sessiontimeout.dart';
import 'package:flutter/material.dart';


class PageFooter {
  static List<Widget> build() {
    return [
      Text(
          Translator.text('AppTaskTracker', 'Task Tracker') + ', ' +
              Translator.text('Common', 'Version') + ' ' +
              Config.appInfo.version
      ),
      Visibility(
        visible: (Config.authStatus.authenticated == true),
        child:
        Row(
            children: [
              Text(Translator.text('Common', 'User') + ': '),
              Text(Config.authStatus.loginName + ', '),
              Text(Translator.text('Common', 'Timeout') + ' '),
              WidgetSessionTimeout(),
            ]
        ),
      ),
    ];
  }
}
