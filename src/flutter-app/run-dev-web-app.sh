#!/bin/bash

cd $(dirname $0)
echo "Running Flutter Project in Working Directory: $(pwd)"

DST_DIR=../../main/resources/static

../../misc/patch-flutter-withCredentials.sh patch

cd TaskTracker

rm -rf ${DST_DIR}
mkdir -p build/web/
ln -s $(pwd)/build/web ${DST_DIR}

rm -f $(pwd)/build/web/lib
ln -s $(pwd)/lib ${DST_DIR}/lib

flutter run -d Chrome --web-hostname=127.0.0.1 --web-port=8200

