#!/usr/bin/env bash
ECHO OFF
ECHO Make sure 'JAVA_HOME' is set to the JDK folder.
ECHO Make sure 'Maven' is on the System Path.
ECHO Building all modules...
mvn $1 dependency:copy-dependencies package