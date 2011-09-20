//--Need to modify--#preprocess
/*
 * EncodingStreamReq.java
 *
 * Copyright (C) 2010 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-12-05 1.11.5Dev15 Provide request constants to test EncodingStreamReader.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
//#ifdef DFULLVERS
package com.substanceofcode.jmunit.utils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Vector;

final public class EncodingStreamReq {
	final static int USE_IN_NO_UTF = 0;
	final static int USE_IN_UTF1 = 1;
	final static int USE_IN_UTF7 = 2;
	final static int USE_IN_UTF8 = 3;
	final static int USE_IN_UTF16BE = 4;
	final static int USE_IN_UTF16LE = 5;
	final static int USE_IN_UTF32BE = 6;
	final static int USE_IN_UTF32LE = 7;
	final static int USE_IN_SINGLE = 0;
	final static int USE_IN_BUF = 1;
	final static int USE_ARR_IN_STR = 0;
	final static int USE_ARR_IN_BARR = 1;
	final static int USE_ARR_IN_INPUT_PARM = 2;
	final static int USE_PARM_TYPE_INP_TYPE = 0;

	final static String[] suserInputType = new String[] {
			"USE_IN_NO_UTF",
			"USE_IN_UTF1",
			"USE_IN_UTF7",
			"USE_IN_UTF8",
			"USE_IN_UTF16BE",
			"USE_IN_UTF16LE",
			"USE_IN_UTF32BE",
			"USE_IN_UTF32LE"};
}
//#endif
//#endif
