#!/bin/bash
# IB IB 2010-11-17 1.11.5Dev14 Have microemu.sh to run microemulator demo of application version.
export CLASSPATH="$CLASSPATH:\
lib/@EMULATORJAR@:lib/@BUILDFILEROOT@.jar:lib/microemu-device-large@FILEVERSPREFIX@.jar:lib/midpapi20@FILEVERSPREFIX@.jar:lib/cldcapi11@FILEVERSPREFIX@.jar:lib/microemu-jsr-75@FILEVERSPREFIX@.jar"
echo "CLASSPATH=$CLASSPATH"
java @EMULATORCLASS@ @MIDLETCLASS@ --device org/microemu/device/large/device.xml
