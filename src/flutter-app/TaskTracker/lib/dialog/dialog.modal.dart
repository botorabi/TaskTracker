/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:flutter/material.dart';

class DialogModal {

  BuildContext _context;

  DialogModal(this._context);

  Future<void> show(String title, String text, bool warn) {
    return showDialog<void>(
        context: _context,
        barrierDismissible: true,
        builder: (_) =>
        new AlertDialog(
          title: new Text(title),
          content: new Text(
            text,
            style: warn ?
                  TextStyle(fontWeight: FontWeight.w300) :
                  TextStyle(color: Colors.black),
          ),
          actions: <Widget>[
            FlatButton(
              child: Text('Close'),
              onPressed: () {
                Navigator.of(_context).pop();
              },
            )
          ],
        )
    );
  }
}
