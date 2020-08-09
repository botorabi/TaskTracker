#!/bin/bash

cd $(dirname $0)

source ../../misc/setup-env.sh

cd TaskTracker

export PATH="$PATH":"$HOME/bin/flutter/.pub-cache/bin"

flutter pub global run devtools

