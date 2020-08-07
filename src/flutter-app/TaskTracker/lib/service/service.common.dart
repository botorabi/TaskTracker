/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

abstract class ServiceCommon {

  static const Map<String, String> HTTP_HEADERS_REST = {
    'Content-type': 'application/json',
    'withCredentials': 'true'
  };
}
