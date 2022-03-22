#!/bin/bash
# This is basing on Leonard Arnold's great article: https://medium.com/swlh/flutter-web-node-js-cors-and-cookies-f5db8d6de882

echo ""
echo "Using flutter directory: ${FLUTTER_PATH}"

if [[ ! -d "${FLUTTER_PATH}" ]]; then
  echo "*** Cannot find flutter directory: '${FLUTTER_PATH}'"
  echo "*** Missing env variable FLUTTER_PATH"
  echo ""
  exit 1
fi

echo ""

if [[ $1 == 'patch' ]]; then
    find ${FLUTTER_PATH}/.pub-cache/hosted/pub.dartlang.org -name "browser_client.dart" -type f -exec sed -i 's/bool withCredentials = false/bool withCredentials = true/g' {} \;
    echo "http package browser client patched!"
elif [[ $1 == "revert" ]]; then
    find ${FLUTTER_PATH}/.pub-cache/hosted/pub.dartlang.org -name "browser_client.dart" -type f -exec sed -i 's/bool withCredentials = true/bool withCredentials = false/g' {} \;
    echo "http package browser client patch reverted!"
else
    echo "Use patch-flutter-withCredentials.sh <patch | revert>"
fi
echo ""

exit 0
