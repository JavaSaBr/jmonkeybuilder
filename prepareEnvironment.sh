#!/usr/bin/env sh

echo "$JAVA_HOME"

exec "$JAVA_HOME/lib/java -version"
exec "$JAVA_HOME/lib/java --list-modules"
