#!/bin/bash

CONFIG_FILE=TaskTracker/lib/config.dart
BASE_URL_PATCHED=http://localhost:8080
BASE_URL_ORIGINAL=

echo ""

if [[ $1 == 'patch' ]]; then
    echo "Patching base URL to '${BASE_URL_PATCHED}' in file ${CONFIG_FILE}"
    sed -i "s|String baseURL.*;|String baseURL = '${BASE_URL_PATCHED}';|1" ${CONFIG_FILE}
elif [[ $1 == "revert" ]]; then
    echo "Reverting base URL to '${BASE_URL_ORIGINAL}' in file ${CONFIG_FILE}"
    sed -i "s|String baseURL.*;|String baseURL = '${BASE_URL_ORIGINAL}';|g" ${CONFIG_FILE}
else
    echo "Use patch-base-url.sh <patch | revert>"
fi
echo ""

exit 0
