#!/bin/bash
# IB IB 2010-11-17 1.11.5Dev14 Have microemu.sh to run microemulator demo of application version.
export CLASSPATH="$CLASSPATH:\
../signedlib/@EMULATORJAR@:../signedlib/@BUILDFILEROOT@.jar:../signedlib/@MICROEMUFNROOT@-device-large@FILEVERSPREFIX@.jar:../signedlib/@MICROEMUPREFIXSIGNED@midpapi20@FILEVERSPREFIX@.jar:../signedlib/@MICROEMUPREFIXSIGNED@cldcapi11@FILEVERSPREFIX@.jar:../signedlib/@MICROEMUFNROOT@-jsr-75@FILEVERSPREFIX@.jar"
echo "CLASSPATH=$CLASSPATH"
java @EMULATORCLASS@ @MIDLETCLASS@ --device org/microemu/device/large/device.xml
