/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:flutter/material.dart';

import '../translator.dart';

class DialogModal {

  BuildContext _context;

  DialogModal(this._context);

  Future<void> show(String title, String text, bool warn) {
    return showDialog<void>(
        context: _context,
        barrierDismissible: true,
        builder: (_) => AlertDialog(
          title: Text(title),
          content: Text(
            text,
            style: warn ?
                  TextStyle(fontWeight: FontWeight.w300) :
                  TextStyle(color: Colors.black),
          ),
          actions: <Widget>[
            Padding(
              padding: EdgeInsets.all(10.0),
              child: RaisedButton(
                padding: EdgeInsets.only(top: 10.0, right: 20.0, left: 20.0, bottom: 10.0),
                child: Text(Translator.text('Common', 'Close')),
                onPressed: () => Navigator.of(_context).pop(),
              ),
            ),
          ],
        )
    );
  }
}
