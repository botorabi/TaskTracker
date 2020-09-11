/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:flutter/material.dart';

abstract class CircleButton {

  static IconButton create(double size, IconData iconData, GestureTapCallback cbOnTab, [String toolTip = ""]) {

    return IconButton(
      iconSize: size,
      icon: Icon(iconData, color: (cbOnTab != null) ? Colors.blue : Colors.grey),
      onPressed: cbOnTab,
      tooltip: toolTip != "" ? toolTip : null,
    );
  }
}
