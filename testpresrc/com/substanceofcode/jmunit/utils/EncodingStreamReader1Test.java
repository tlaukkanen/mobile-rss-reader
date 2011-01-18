//--Need to modify--#preprocess
/*
 * EncodingStreamReader1Test.java
 *
 * Copyright (C) 2009 Irving Bunton
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
 * IB 2010-04-05 1.11.4RC1 Allow logging of characters for different expected tokens.
 * IB 2010-04-30 1.11.5RC2 Test line feeds in code.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Test EncodingStreamReader.
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
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Vector;

import com.substanceofcode.utils.EncodingStreamReader;
import com.substanceofcode.utils.EncodingUtil;

public class EncodingStreamReader1Test extends EncodingStreamReaderTest
{

	//#ifdef DTEST
	//#ifdef DLOGGING
	private boolean logReadChar = traceLoggable; // or traceLoggable
	//#endif
	//#endif

	public EncodingStreamReader1Test() {
		super(12, "1");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				encodingStreamReaderRead1();
				break;
			case 1:
				encodingStreamReaderRead2();
				break;
			case 2:
				encodingStreamReaderRead3();
				break;
			case 3:
				encodingStreamReaderRead4();
				break;
			case 4:
				encodingStreamReaderRead5();
				break;
			case 5:
				encodingStreamReaderRead6();
				break;
			case 6:
				encodingStreamReaderRead7();
				break;
			case 7:
				encodingStreamReaderRead8();
				break;
			case 8:
				encodingStreamReaderRead9();
				break;
			case 9:
				encodingStreamReaderRead10();
				break;
			case 10:
				encodingStreamReaderRead11();
				break;
			case 11:
				encodingStreamReaderRead12();
				break;
			default:
				super.test(testNumber);
		}
	}

	public void encodingStreamReaderRead1() throws Throwable {
		String mname = "encodingStreamReaderRead1";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("a")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0,
				new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead2() throws Throwable {
		String mname = "encodingStreamReaderRead2";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF1_BOM, UTF8_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_1, encInstance.UTF_8};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("ab")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0,  new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF1,
				EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead3() throws Throwable {
		String mname = "encodingStreamReaderRead3";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("abc")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead4() throws Throwable {
		String mname = "encodingStreamReaderRead4";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("abcd")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead5() throws Throwable {
		String mname = "encodingStreamReaderRead5";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("abcde")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead6() throws Throwable {
		String mname = "encodingStreamReaderRead6";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer(
				"abcde0123456789012834056790abcde0123456789012834056790")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead7() throws Throwable {
		String mname = "encodingStreamReaderRead7";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("abcde")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead8() throws Throwable {
		String mname = "encodingStreamReaderRead8";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		int[][] inputData = new int[][]
			{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead9() throws Throwable {
		String mname = "encodingStreamReaderRead9";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF1_BOM, UTF8_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_1, encInstance.UTF_8};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF1,
				EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead10() throws Throwable {
		String mname = "encodingStreamReaderRead10";
		byte[][] bomReqs = new byte[][] {UTF7_BOM1, UTF7_BOM2, UTF7_BOM3, UTF7_BOM4}; 
		int[] expEncs = new int[] {encInstance.UTF_7, encInstance.UTF_7, encInstance.UTF_7, encInstance.UTF_7};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {new StringBuffer("ab")};
		Object[] sinputParms = new StringBuffer[0];
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF7,
				EncodingStreamReq.USE_IN_UTF7, EncodingStreamReq.USE_IN_UTF7, EncodingStreamReq.USE_IN_UTF1},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead11() throws Throwable {
		String mname = "encodingStreamReaderRead11";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF16BE_BOM, UTF16LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_16, encInstance.UTF_16};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1};
		Object[] sinputParms = new StringBuffer[0];
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF16BE,
				EncodingStreamReq.USE_IN_UTF16LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead12() throws Throwable {
		String mname = "encodingStreamReaderRead12";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF32BE_BOM, UTF32LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_32, encInstance.UTF_32};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 1}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1};
		Object[] sinputParms = new StringBuffer[0];
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF32BE,
				EncodingStreamReq.USE_IN_UTF32LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

}
//#endif
//#endif
