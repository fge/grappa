#!/bin/bash

#
# We don't want to use the gradle daemon (new in 2.4) for releases
#

./gradlew --no-daemon --recompile-scripts clean test uploadArchives

