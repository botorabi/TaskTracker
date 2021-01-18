/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/common/button.id.dart';
import 'package:flutter/material.dart';

import '../translator.dart';

class DialogViewProgress {

  BuildContext _context;

  DialogViewProgress(this._context);

  Future<String> show(String title, String text) {
    return showDialog<String>(
        context: _context,
        barrierDismissible: true,
        builder: (_) => AlertDialog(
          title: Text(title),
          content: Text(
            text,
            style: TextStyle(color: Colors.black),
          ),
          actions: <Widget>[
            Padding(
              padding: EdgeInsets.all(10.0),
              child: RaisedButton(
                padding: EdgeInsets.only(top: 10.0, right: 20.0, left: 20.0, bottom: 10.0),
                child: Text(Translator.text('Common', 'Copy')),
                onPressed: () => Navigator.of(_context).pop(ButtonID.COPY),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(10.0),
              child: RaisedButton(
                padding: EdgeInsets.only(top: 10.0, right: 20.0, left: 20.0, bottom: 10.0),
                child: Text(Translator.text('Common', 'Close')),
                onPressed: () => Navigator.of(_context).pop(ButtonID.CANCEL),
              ),
            ),
          ],
        )
    );
  }
}
