#!/bin/bash

cd $(dirname $0)
echo "Building Flutter Project in Working Directory: $(pwd)"

DST_DIR=../../main/resources/static

../../misc/patch-flutter-withCredentials.sh revert

cd TaskTracker
flutter pub get
flutter build -d Chrome web
rm -rf ${DST_DIR}
cp -r build/web ${DST_DIR}/

