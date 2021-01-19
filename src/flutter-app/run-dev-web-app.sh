#!/bin/bash

cd $(dirname $0)
echo "Running Flutter Project in Working Directory: $(pwd)"

source ../../misc/setup-env.sh
./patch-flutter-withCredentials.sh patch
./patch-base-url.sh patch

cd TaskTracker

#flutter run -d Chrome --web-hostname=127.0.0.1 --web-port=8200
flutter run  --web-hostname=127.0.0.1 --web-port=8200

