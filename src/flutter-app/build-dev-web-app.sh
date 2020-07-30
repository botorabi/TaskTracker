#!/bin/bash

DST_DIR=../../main/resources/static

../../misc/patch-flutter-withCredentials.sh patch

cd TaskTracker
flutter build -d Chrome web
rm -rf ${DST_DIR}
ln -s $(pwd)/build/web ${DST_DIR}

