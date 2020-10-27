#!/bin/bash

CONFIG_FILE=TaskTracker/lib/config.dart
BASE_URL_PATCHED=http://localhost:8080

if env | grep -q ^TASK_TRACKER_BASE_URL=; then
  echo "Using base URL from env TASK_TRACKER_BASE_URL: ${TASK_TRACKER_BASE_URL}"
  BASE_URL_ORIGINAL=${TASK_TRACKER_BASE_URL}
else
  echo "env variable TASK_TRACKER_BASE_URL not set, using default base URL"
  BASE_URL_ORIGINAL=
fi

echo ""

if [[ $1 == 'patch' ]]; then
    echo "Patching base URL to '${BASE_URL_PATCHED}' in file ${CONFIG_FILE}"
    sed -i "s|String BASE_URL.*;|String BASE_URL = '${BASE_URL_PATCHED}';|1" ${CONFIG_FILE}
elif [[ $1 == "revert" ]]; then
    echo "Reverting base URL to '${BASE_URL_ORIGINAL}' in file ${CONFIG_FILE}"
    sed -i "s|String BASE_URL.*;|String BASE_URL = '${BASE_URL_ORIGINAL}';|g" ${CONFIG_FILE}
else
    echo "Use patch-base-url.sh <patch | revert>"
fi
echo ""

exit 0
