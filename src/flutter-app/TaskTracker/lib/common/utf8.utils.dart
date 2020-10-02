/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class Utf8Utils {

  static String fromUtf8(final String input) {
    return utf8.decode(input.runes.toList());
  }
}