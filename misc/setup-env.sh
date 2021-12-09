#!/bin/bash

#export ANDROID_SDK_ROOT=${HOME}/bin/android
export FLUTTER_PATH=${HOME}/snap/flutter/common/flutter/.pub-cache/bin
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
export PATH=${JAVA_HOME}/bin:${FLUTTER_PATH}:${PATH}
