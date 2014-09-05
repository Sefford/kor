#!/bin/sh
#
# ---------------------------------------------------------------------
# Dependency upload
# ---------------------------------------------------------------------
#

#Upload Facebook to Local repo
mvn install:install-file -Dfile=./support-v4-19.1.0.jar -DgroupId=com.android.support -DartifactId=support-v4 -Dversion=19.1.0