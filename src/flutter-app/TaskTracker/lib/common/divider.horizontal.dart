/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class HorizontalDivider extends StatelessWidget {

  HorizontalDivider({Key key, this.color = Colors.grey, this.insets = const EdgeInsets.only(left: 0.0, right: 0.0, bottom: 5.0)}) : super(key: key);

  final Color color;
  final EdgeInsets insets;

  @override
  Widget build(BuildContext context) {
    return Container(height: 1,
      margin: const EdgeInsets.only(left: 0.0, right: 0.0, bottom: 5.0),
      decoration: BoxDecoration(
        color: color,
        boxShadow: [
          BoxShadow(
            color: color,
            offset: Offset(0, 0),
            blurRadius: 0.3,
            spreadRadius: 0,
          )
        ],
      ),
    );
  }
}
