//--Need to modify--#preprocess
/*
 * EncodingStreamReader2Test.java
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

import com.substanceofcode.jmunit.utils.XmlParserTest;

public class EncodingStreamReader2Test extends EncodingStreamReaderTest
{
	public EncodingStreamReader2Test() {
		super(7, "2");
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
			default:
				super.test(testNumber);
		}
	}

	public void encodingStreamReaderRead1() throws Throwable {
		String mname = "encodingStreamReaderRead1";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF16BE_BOM, UTF16LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_16, encInstance.UTF_16};
		int[][] inputData = new int[][] {
				{EncodingStreamReq.USE_ARR_IN_STR, 0, 1},
				{EncodingStreamReq.USE_ARR_IN_INPUT_PARM, EncodingStreamReq.USE_PARM_TYPE_INP_TYPE, 1},
				{EncodingStreamReq.USE_ARR_IN_STR, 1, 2}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {
				START_XML_PROLOGUE1, END_XML_PROLOGUE1, RSS091};
		Object[] sinputParms = UTF_ENCODING_STRS;
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF16BE,
				EncodingStreamReq.USE_IN_UTF16LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead2() throws Throwable {
		String mname = "encodingStreamReaderRead2";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF32BE_BOM, UTF32LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_32, encInstance.UTF_32};
		int[][] inputData = new int[][] {
				{EncodingStreamReq.USE_ARR_IN_STR, 0, 1},
				{EncodingStreamReq.USE_ARR_IN_INPUT_PARM, EncodingStreamReq.USE_PARM_TYPE_INP_TYPE, 1},
				{EncodingStreamReq.USE_ARR_IN_STR, 1, 2}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {
				START_XML_PROLOGUE1, END_XML_PROLOGUE1, RSS091};
		Object[] sinputParms = UTF_ENCODING_STRS;
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF32BE,
				EncodingStreamReq.USE_IN_UTF32LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead3() throws Throwable {
		String mname = "encodingStreamReaderRead3";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF16BE_BOM, UTF16LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_16, encInstance.UTF_16};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 2},
				{EncodingStreamReq.USE_ARR_IN_INPUT_PARM, EncodingStreamReq.USE_PARM_TYPE_INP_TYPE, 1},
				{EncodingStreamReq.USE_ARR_IN_STR, 2, 3}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1,
				START_XML_PROLOGUE1, END_XML_PROLOGUE1, RSS091, COMMENT1};
		Object[] sinputParms = UTF_ENCODING_STRS;
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF16BE,
				EncodingStreamReq.USE_IN_UTF16LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead4() throws Throwable {
		String mname = "encodingStreamReaderRead4";
		byte[][] bomReqs = new byte[][] {NO_BOM, UTF32BE_BOM, UTF32LE_BOM}; 
		int[] expEncs = new int[] {encInstance.NOT_UTF, encInstance.UTF_32, encInstance.UTF_32};
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, 2},
				{EncodingStreamReq.USE_ARR_IN_INPUT_PARM, EncodingStreamReq.USE_PARM_TYPE_INP_TYPE, 1},
				{EncodingStreamReq.USE_ARR_IN_STR, 2, 3}};
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = new StringBuffer[] {DOC_TYPE1,
				START_XML_PROLOGUE1, END_XML_PROLOGUE1, RSS091, COMMENT1};
		Object[] sinputParms = UTF_ENCODING_STRS;
		int[] useInpType = new int[] {
				EncodingStreamReq.USE_IN_NO_UTF,
				EncodingStreamReq.USE_IN_UTF32BE,
				EncodingStreamReq.USE_IN_UTF32LE};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 0, useInpType,
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead5() throws Throwable {
		String mname = "encodingStreamReaderRead5";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = XmlParserTest.getXmlParseData1();
		Object[] sinputParms = new Object[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, sinputData.length}};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 2,
				new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead6() throws Throwable {
		String mname = "encodingStreamReaderRead6";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = XmlParserTest.getXmlParseData2();
		Object[] sinputParms = new Object[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, sinputData.length}};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 2,
				new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

	public void encodingStreamReaderRead7() throws Throwable {
		String mname = "encodingStreamReaderRead7";
		byte[][] bomReqs = new byte[0][0]; 
		int[] expEncs = new int[0];
		byte[][] binputData = new byte[0][0];
		Object[] sinputData = XmlParserTest.getXmlParseData3();
		Object[] sinputParms = new Object[0];
		int[][] inputData = new int[][]
				{{EncodingStreamReq.USE_ARR_IN_STR, 0, sinputData.length}};
		encodingStreamReaderTestSub(mname, bomReqs, expEncs, inputData, binputData,
				sinputData, sinputParms, 4,
				new int[] {EncodingStreamReq.USE_IN_UTF8},
				EncodingStreamReq.USE_IN_SINGLE, 1);
	}

}
//#endif
//#endif