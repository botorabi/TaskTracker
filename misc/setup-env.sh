#!/bin/bash

export ANDROID_SDK_ROOT=${HOME}/bin/android
export FLUTTER_PATH=${HOME}/bin/flutter/bin
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
export PATH=${JAVA_HOME}/bin:${FLUTTER_PATH}:${PATH}
export PATH="$PATH":"$HOME/bin/flutter/.pub-cache/bin"
