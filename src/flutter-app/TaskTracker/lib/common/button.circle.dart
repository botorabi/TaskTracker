/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:flutter/material.dart';

abstract class CircleButton {

  static ClipOval create(
      double size, IconData iconData, double iconSize,
      GestureTapCallback cbOnTab) {

    return ClipOval(
      child: Material(
        color: (cbOnTab != null) ? Colors.blue : Colors.grey,
        elevation: 2.0,
        child: InkWell(
          splashColor: (cbOnTab != null) ? Colors.blueGrey : Colors.grey,
          child: SizedBox(
              width: size,
              height: size,
              child: Icon(iconData, color: Colors.white, size: iconSize)
          ),
          onTap: cbOnTab,
        ),
      ),
    );
  }
}
