#!/bin/sh

APPDIR=$(readlink -f "$0")
APPDIR=$(dirname "$APPDIR")
java -Djna.nosys=true -Djava.library.path="$APPDIR:$APPDIR/lib" -cp "$APPDIR:$APPDIR/lib/oscilloscope_4ch.jar:$APPDIR/lib/core.jar:$APPDIR/lib/jogl-all.jar:$APPDIR/lib/gluegen-rt.jar:$APPDIR/lib/jogl-all-natives-linux-amd64.jar:$APPDIR/lib/gluegen-rt-natives-linux-amd64.jar:$APPDIR/lib/jssc.jar:$APPDIR/lib/serial.jar" oscilloscope_4ch "$@"