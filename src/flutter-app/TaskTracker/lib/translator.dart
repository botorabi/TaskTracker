/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/localization/all.translation.dart';

class TranslatorCore {
  String _locale = '';

  TranslatorCore(this._locale);

  void setLocale(String locale) {
    _locale = locale;
  }

  String text(String context, String text) {
    if (Translations.containsKey(context)) {
      if (Translations[context].containsKey(text)) {
        return Translations[context][text][_locale];
      }
    }
    print("WARN: no translation exists, taking original text, context: " + context + ", text: " + text);
    return text;
  }
}

final Translator = TranslatorCore(Config.locale);
