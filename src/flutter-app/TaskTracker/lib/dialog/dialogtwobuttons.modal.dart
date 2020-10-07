/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';

/*
 * Example for using the Two-Button-Dialog
 *
 * var button = await DialogTwoButtonsModal(context).show('Attention', 'You really want to delete the user?', ButtonID.YES, ButtonID.NO);
 * if (button != 'Yes') {
 *   return;
 * }
 */
class DialogTwoButtonsModal {

  BuildContext _context;

  DialogTwoButtonsModal(this._context);

  Future<String> show(String title, String text, String buttonLabel1, String buttonLabel2) {
    return showDialog<String>(
      context: _context,
      barrierDismissible: true,
      builder: (_) => AlertDialog(
        title: Text(title),
        content: Text(
          text,
          style: TextStyle(fontWeight: FontWeight.w300),
        ),
        actions: <Widget>[
          FlatButton(
            child: Text(Translator.text('Common', buttonLabel2)),
            onPressed: () {
              Navigator.of(_context).pop(buttonLabel2);
            },
          ),
          FlatButton(
            child: Text(Translator.text('Common', buttonLabel1)),
            onPressed: () {
              Navigator.of(_context).pop(buttonLabel1);
            },
          ),
        ],
      )
    );
  }
}
