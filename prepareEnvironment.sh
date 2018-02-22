#!/usr/bin/env sh

echo "$JAVA_HOME"

exec "$JAVA_HOME/bin/java -version"
exec "$JAVA_HOME/bin/java --list-modules"
