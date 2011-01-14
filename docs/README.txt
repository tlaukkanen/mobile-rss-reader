Program files for Mobile RSS Reader with source code located at 
http://code.google.com/p/mobile-rss-reader/.

Choosing a program file
-----------------------

NOTE:  All files ending in *.c* or *cod.jad, that is having a 'c' after the
	   '.' are ONLY for Blackberry.  So, if you're not using a Blackberry, just
	   ignore those.

It is important to choose the right program file.  This is because unlike
on the desktop, choosing a program file which uses features not available
on the phone may cause the program to not run WITHOUT an error message.
Phone makers are not required to give such a message, so many do not let
the user know that why the program is not allowed to run.  So, to avoid
frustration, if you try to run one version of the program and you do not
see anything to show that it was run, try a version with less features.
There is a web site http://www.mobref.com/device/ which allows you to look
up which features are supported on most phones.  You can also install
the MIDP 1.0
version and go to settings and look at phone MIDP version and JSR 75 available
fields at the bottom.  If MIDP version is 2.0 or higher, you can use
versions beginning with midp20_.  If JSR75 available is true, you can use
versions with jsr75_ in the name.

For older phones, programs and directories beginning with midp10_ are for
MIDP 1.0 phone versions.  These program versions will also run on other
phones, but these program
versions have less features as the language has improved since then.

rim_midp10_cldc10_@STAGEFILE@RSSReader.cod - MIDP 1.0 Blackberry COD
rim_midp10_cldc10_@STAGEFILE@RSSReader.jar - MIDP 1.0 Blackberry JAR
rim_midp10_cldc10_@STAGEFILE@RSSReader.cso - MIDP 1.0 Blackberry CSO
midp10_cldc10_@STAGEFILE@RSSReader.jad - MIDP 1.0 jad
midp10_cldc10_@STAGEFILE@RSSReader.jar - MIDP 1.0 jar


midp20_ programs/directories (without jsr75_) are for phones with MIDP 2.0.
This should work for all recent phones.  

rim_midp20_cldc10_@STAGEFILE@RSSReader.cod - MIDP 2.0 Blackberry COD
rim_midp20_cldc10_@STAGEFILE@RSSReader.jar - MIDP 2.0 Blackberry JAR
rim_midp20_cldc10_@STAGEFILE@RSSReader.cso - MIDP 2.0 Blackberry CSO
midp20_cldc10_@STAGEFILE@RSSReader.jad - MIDP 2.0 jad
midp20_cldc10_@STAGEFILE@RSSReader.jar - MIDP 2.0 jar



midp20_jsr75_ programs .  For newer phones and usually with media (e.g. MP3)
	    capabilities, use files prefixed with
		midp20_jsr75_ if the phones have MIDP-2.0 and JSR-75 use midp20_jsr75_
		version.  To
		tell if you have a phone which supports JSR-75, you can use either
		of the other versions and go to Settings and look at phone jsr75
		if it says true, it supports jsr75 (this is different than progarm
		jsr75 which shows the version of the progam not the phone).

rim_midp20_cldc10_jsr75_@STAGEFILE@RSSReader.cod - MIDP 2.0 and JSR-75 Blackberry COD
rim_midp20_cldc10_jsr75_@STAGEFILE@RSSReader.jar - MIDP 2.0 and JSR-75 Blackberry JAR
rim_midp20_cldc10_jsr75_@STAGEFILE@RSSReader.cso - MIDP 2.0 and JSR-75 Blackberry CSO
midp20_cldc10_jsr75_@STAGEFILE@RSSReader.jad - MIDP 2.0 and JSR-75 jad
midp20_cldc10_jsr75_@STAGEFILE@RSSReader.jar - MIDP 2.0 and JSR-75 jar

