//--Need to modify--#preprocess
/*
 * EncodingStreamReaderTest.java
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
 * IB 2011-01-14 1.11.5Alpha15 Provide common methods and vars to test EncodingStreamReader.
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

import com.substanceofcode.jmunit.utilities.BaseTestCase;

abstract public class EncodingStreamReaderTest extends BaseTestCase
{
	static final StringBuffer DOC_TYPE1 = new StringBuffer("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
	static final StringBuffer START_XML_PROLOGUE1 = new StringBuffer("<?xml version=\"1.0\" encoding=\"");
	static final StringBuffer END_XML_PROLOGUE1 = new StringBuffer("\"?>");
	static final StringBuffer[] UTF_ENCODING_STRS = new StringBuffer[] {
			new StringBuffer("utf-1"), new StringBuffer("utf-7"),
			new StringBuffer("utf-8"),
			new StringBuffer("utf-16"), new StringBuffer("utf-16be"), new StringBuffer("utf-16le"), new StringBuffer("utf-32"), new StringBuffer("utf-32be"),
			new StringBuffer("utf-32le")};
	static final StringBuffer COMMENT1 = new StringBuffer("<!-- comment1 -->");
	static final StringBuffer RSS091 = new StringBuffer("<rss version=\"0.91\">");
	static final byte[] NO_BOM = new byte[0];
	static final byte[] UTF1_BOM = new byte[] {(byte)0x0f7, (byte)0x64, (byte)0x4c};
	static final byte[] UTF7_BOM1 = new byte[] {(byte)0x02b, (byte)0x02f, (byte)0x076, (byte)0x38};
	static final byte[] UTF7_BOM2 = new byte[] {(byte)0x02b, (byte)0x02f, (byte)0x076, (byte)0x39};
	static final byte[] UTF7_BOM3 = new byte[] {(byte)0x02b, (byte)0x02f, (byte)0x076, (byte)0x2b};
	static final byte[] UTF7_BOM4 = new byte[] {(byte)0x02b, (byte)0x02f, (byte)0x076, (byte)0x2f};
	static final byte[] UTF8_BOM = new byte[] {(byte)0x0ef, (byte)0x0bb, (byte)0x0bf};
	static final byte[] UTF16LE_BOM = new byte[] {(byte)0x0ff, (byte)0x0fe};
	static final byte[] UTF16BE_BOM = new byte[] {(byte)0x0fe, (byte)0x0ff};
	static final byte[] UTF32LE_BOM = new byte[] {(byte)0x0ff, (byte)0x0fe, (byte)0x00, (byte)0x00};
	static final byte[] UTF32BE_BOM = new byte[] {(byte)0x00, (byte)0x00, (byte)0x0fe, (byte)0x0ff};
	static final byte[] EMPTY_BARR = new byte[0];
	final EncodingUtil encInstance;
	//#ifdef DTEST
	//#ifdef DLOGGING
	protected boolean logReadChar = traceLoggable; // or traceLoggable
	protected boolean logdConstChar = traceLoggable; // or traceLoggable
	protected boolean logConvChar = traceLoggable; // or traceLoggable
	protected boolean logCmpChar = traceLoggable; // or traceLoggable
	protected boolean logPcmpChar = traceLoggable; // or traceLoggable
	//#endif
	//#endif

	public EncodingStreamReaderTest(int nbrTests, String stestCaseNbr) {
		super(nbrTests, "EncodingStreamReader" + stestCaseNbr + "Test");
		encInstance = EncodingUtil.getInstance();
	}

    public void encodingStreamReaderTestSub(final String mname,
			byte[][] bomReqs, int[] expEncs, int[][] inputData, byte[][] binputData,
			Object[] sinputData, Object[] sinputParms, int gt2pro, int[] useInpType, int useRdType,
			int nbrBytes)
	throws Throwable {
		int bix = 0;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + " EncodingStreamReaderTestSub");
			//#endif
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " EncodingStreamReaderTestSub bomReqs.length,inputData.length,binputData.length,sinputData.length=" + bomReqs.length + "," + inputData.length + "," + binputData.length + "," + sinputData.length);} ;
			//#endif
			for (bix = 0; (bix < bomReqs.length) || (bomReqs.length == 0);) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int barrix = 0;
				final String suserTypeRep = "useInpType[bix]";
				final String suserTypeVal =
					EncodingStreamReq.suserInputType[useInpType[bix]];
				// EncodingStreamReader.logiarr( useInpType, bix)
				if ((bomReqs.length == 0) || (bomReqs[bix].length == 0)) {
					//#ifdef DLOGGING
					if (finestLoggable && (bomReqs.length == 0)) {logger.finest(mname + " EncodingStreamReaderTestSub Continuing bix,bomReqs.length," + suserTypeRep + "=" +  bix + "," + bomReqs.length + "," + suserTypeVal);} ;
					if (finestLoggable && (bomReqs.length > 0) && (bomReqs[bix].length == 0)) {logger.finest(mname + " EncodingStreamReaderTestSub Continuing bix,bomReqs[bix].length," + suserTypeRep + "=" +  bix + "," + bomReqs[bix].length + "," + suserTypeVal);}
					//#endif
				} else {
					byte[] cbomb = bomReqs[bix];
					wrbos(bos, crbos(mname, cbomb));
					barrix = cbomb.length;
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " EncodingStreamReaderTestSub Continuing bix,bomReqs.length,cbomb[0],cbomb[bomReqs.length - 1]=" +  bix + "," + bomReqs.length + "," + EncodingStreamReader.logBarr(cbomb, 0) + "," + EncodingStreamReader.logBarr(cbomb, bomReqs.length - 1));} ;
					//#endif
				}
				for (int j = 0; j < inputData.length; j++) {
					int[] inputType = inputData[j];
					//#ifdef DLOGGING
					if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub inputType[0],inputType[1],useInpType[bix]=" + "," + EncodingStreamReader.logiarr(inputType, 0) + "," + EncodingStreamReader.logiarr(inputType, 1) + "," + suserTypeVal);} ;
					//#endif
					switch (inputType[0]) {
						case EncodingStreamReq.USE_ARR_IN_BARR:
							if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF16BE) {
								wrbos(bos, cr16bebos(mname, binputData[inputType[1]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF16LE) {
								wrbos(bos, cr16lebos(mname, binputData[inputType[1]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF32BE) {
								wrbos(bos, cr32bebos(mname, binputData[inputType[1]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF32LE) {
								wrbos(bos, cr32lebos(mname, binputData[inputType[1]]));
							} else {
								wrbos(bos, crbos(mname, binputData[inputType[1]]));
							}
							break;
						case EncodingStreamReq.USE_ARR_IN_STR:
							int off = inputType[1];
							for (int i = 0; i < inputType[2]; i++) {
								//#ifdef DLOGGING
								if (logdConstChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub bix,i,off,sinputData[off]=" + bix + "," + i + "," + off + "," + EncodingStreamReader.logsoarr(sinputData, off));} ;
								//#endif
								switch (useInpType[bix]) {
									case EncodingStreamReq.USE_IN_UTF16BE:
										wrbos(bos, cr16bebos(mname, sinputData[off++]));
										break;
									case EncodingStreamReq.USE_IN_UTF16LE:
										wrbos(bos, cr16lebos(mname, sinputData[off++]));
										break;
									case EncodingStreamReq.USE_IN_UTF32BE:
										wrbos(bos, cr32bebos(mname, sinputData[off++]));
										break;
									case EncodingStreamReq.USE_IN_UTF32LE:
										wrbos(bos, cr32lebos(mname, sinputData[off++]));
										break;
									default:
										wrbos(bos, crbos(mname, sinputData[off++]));
								}
							}
							break;
						case EncodingStreamReq.USE_ARR_IN_INPUT_PARM:
							if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF16BE) {
								wrbos(bos, cr16bebos(mname, sinputParms[useInpType[bix]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF16LE) {
								wrbos(bos, cr16lebos(mname, sinputParms[useInpType[bix]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF32BE) {
								wrbos(bos, cr32bebos(mname, sinputParms[useInpType[bix]]));
							} else if (useInpType[bix] ==
									EncodingStreamReq.USE_IN_UTF32LE) {
								wrbos(bos, cr32lebos(mname, sinputParms[useInpType[bix]]));
							} else {
								wrbos(bos, crbos(mname, sinputParms[useInpType[bix]]));
							}
							break;
						default:
						break;
					}
				}
				int charLen;
				switch (useInpType[bix]) {
					case EncodingStreamReq.USE_IN_UTF1:
					case EncodingStreamReq.USE_IN_UTF7:
					case EncodingStreamReq.USE_IN_UTF8:
						charLen = 1;
						break;
					case EncodingStreamReq.USE_IN_UTF16BE:
					case EncodingStreamReq.USE_IN_UTF16LE:
						charLen = 2;
						break;
					case EncodingStreamReq.USE_IN_UTF32BE:
					case EncodingStreamReq.USE_IN_UTF32LE:
						charLen = 4;
						break;
					default:
						charLen = 1;
						break;
				}

				byte[] barr = bos.toByteArray();
				ByteArrayInputStream bis = new ByteArrayInputStream(barr);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " EncodingStreamReaderTestSub charLen,barr.length=" + charLen + "," + barr.length);} ;
				//#endif
				//#ifdef DLOGGING
				if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub barrix,barr[0],barr[barrix],barr[barr.length - 1]=" + "," + barrix + "," + EncodingStreamReader.logBarr(barr, 0) + "," + EncodingStreamReader.logBarr(barr, barrix) + "," + EncodingStreamReader.logBarr(barr, barr.length - 1));} ;
				//#endif
				EncodingUtil encodingUtil = EncodingUtil.getEncodingUtil(bis);
				EncodingStreamReader esr = encodingUtil.getEncodingStreamReader();
				//#ifdef DLOGGING
				//#ifdef DTEST
				if (logReadChar) {
					esr.setLogChar(logReadChar);
				}
				//#endif
				//#endif
				int prevCharacter = -1;
				int inputCharacter;
				boolean firstRead = true;
				int ngts = 0;
				if (useRdType == EncodingStreamReq.USE_IN_SINGLE) {
					while (((inputCharacter = esr.read()) != -1) &&
							(barrix < barr.length))  {
						//#ifdef DLOGGING
						if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub inputCharacter=" + "," + EncodingStreamReader.logInpChar(inputCharacter) + "," + EncodingStreamReader.logBarr(barr, barrix));} ;
						//#endif
						if (firstRead) {
							firstRead = false;
							if (bomReqs.length != 0) {
								String assertmsg = "bix,expEncs[bix],esr.getBitNbrDoc()=" + bix + "," + expEncs[bix] + "," + esr.getBitNbrDoc();
								//#ifdef DLOGGING
								if (finestLoggable) {logger.finest(mname + " EncodingStreamReaderTestSub " + assertmsg);} ;
								//#endif
								assertEquals(assertmsg, expEncs[bix],
										esr.getBitNbrDoc());
							}
						}
						if ((inputCharacter == '>') && (++ngts == gt2pro)) {
							esr.setGetPrologue(false);
							esr.setModEncoding(esr.isModBit16o32());
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest(mname + " EncodingStreamReaderTestSub got prologue inputCharacter=" + EncodingStreamReader.logInpChar(inputCharacter));} ;
							//#endif
						}
						//#ifdef DLOGGING
						if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub inputCharacter=" + EncodingStreamReader.logInpChar(inputCharacter));} ;
						//#endif
						String assertmsgPrefix = mname + " EncodingStreamReaderTestSub bix,barrix,barr[barrix],inputCharacter=" + bix + "," + barrix + ",";
						String assertmsgMid = bix + "," + barrix;
						switch (useInpType[bix]) {
							case EncodingStreamReq.USE_IN_UTF16BE:
								assertEquals(assertmsgPrefix + EncodingStreamReader.logInpChar(cr16beint(mname, barr, barrix)) + "," + EncodingStreamReader.logInpChar(inputCharacter), cr16beint(mname, barr, barrix), inputCharacter);
								break;
							case EncodingStreamReq.USE_IN_UTF16LE:
								assertEquals(assertmsgPrefix + EncodingStreamReader.logInpChar(cr16leint(mname, barr, barrix))  + "," + inputCharacter, cr16leint(mname, barr, barrix), inputCharacter);
								break;
							case EncodingStreamReq.USE_IN_UTF32BE:
								assertEquals(assertmsgPrefix + EncodingStreamReader.logInpChar(cr32beint(mname, barr, barrix))  + "," + inputCharacter, cr32beint(mname, barr, barrix), inputCharacter);
								break;
							case EncodingStreamReq.USE_IN_UTF32LE:
								assertEquals(assertmsgPrefix + EncodingStreamReader.logInpChar(cr32leint(mname, barr, barrix))  + "," + inputCharacter, cr32leint(mname, barr, barrix), inputCharacter);
								break;
							case EncodingStreamReq.USE_IN_UTF1:
							case EncodingStreamReq.USE_IN_UTF7:
							case EncodingStreamReq.USE_IN_UTF8:
							default:
								assertEquals(assertmsgPrefix + EncodingStreamReader.logInpChar(cr8int(mname, barr, barrix))  + "," + inputCharacter, cr8int(mname, barr, barrix), inputCharacter);
								break;
						}
						barrix += charLen;
						prevCharacter = inputCharacter;
					}
					//#ifdef DLOGGING
					if (logReadChar && traceLoggable) {logger.trace(mname + "  EncodingStreamReaderTestSub before assertTrue bix,inputCharacter,barrix,barr.length=" + bix + "," + EncodingStreamReader.logInpChar(inputCharacter) + "," + barrix + "," + barr.length);}
					//#endif
					assertTrue(mname + " EncodingStreamReaderTestSub bix,inputCharacter,barrix,barr.length truth compare (inputCharacter == -1) && (barrix >= barr.length)=" + bix + "," + EncodingStreamReader.logInpChar(inputCharacter) + "," + barrix + "," + barr.length, ((inputCharacter == -1) && (barrix >= barr.length)));
				} else if (useRdType == EncodingStreamReq.USE_IN_BUF) {
					char[] cbuf = new char[100];
					int offsetRead = 0;
					int lenRead;
					if ((lenRead = esr.read(cbuf, 0, cbuf.length)) <= 0) {
						inputCharacter = -1;
					} else {
						inputCharacter = (int)cbuf[0];
					}
					while (inputCharacter != -1) {
						//#ifdef DLOGGING
						if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub barrix,inputCharacter,barr[barrix]=" + barrix + "," + EncodingStreamReader.logInpChar(inputCharacter) + "," + EncodingStreamReader.logBarr(barr, barrix));} ;
						if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub inputCharacter=" + "," + EncodingStreamReader.logInpChar(inputCharacter));} ;
						//#endif
						barrix++;
						if ((lenRead = esr.read(cbuf, 0, cbuf.length)) <= 0) {
							inputCharacter = -1;
						} else {
							inputCharacter = (int)cbuf[0];
						}
					}
					//#ifdef DLOGGING
					if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub end while inputCharacter=" + "," + EncodingStreamReader.logInpChar(inputCharacter));} ;
					//#endif
				} else {
					//#ifdef DLOGGING
					logger.severe(mname + " EncodingStreamReaderTestSub invalid useRdType=" + useRdType);
					//#endif
					inputCharacter = -1;
				}
				if (bomReqs.length != 0) {
					if ((bix + 1) >= bomReqs.length) {
						break;
					} else {
						bix++;
					}
				} else {
					//#ifdef DLOGGING
					if (logReadChar && traceLoggable) {logger.trace(mname + " EncodingStreamReaderTestSub end while inputCharacter=" + "," + EncodingStreamReader.logInpChar(inputCharacter));} ;
					//#endif
					break;
				}
			}
			//#ifdef DLOGGING
			logger.info("Test " + mname + " PASSED.");
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " EncodingStreamReaderTestSub failure bix=" + bix,e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	int cr32beint(String mname, byte[] barr, int barrix) {
		//#ifdef DLOGGING
		if (logPcmpChar && traceLoggable) {logger.trace(mname + " cr32beint barrix,rtn=" + barrix + "," + EncodingStreamReader.logBarr(barr, barrix) + "," + EncodingStreamReader.logBarr(barr, barrix + 1));}
		//#endif
		int inputCharacter = 0;
		for (int i = 0; i <= 3; i++) { 
			inputCharacter <<= 8;
			inputCharacter |= (int)barr[barrix++];
		}
		//#ifdef DLOGGING
		int rtn =
			//#else
			return
			//#endif
			inputCharacter;
		//#ifdef DLOGGING
		if (logCmpChar && traceLoggable) {logger.trace(mname + " cr32beint barrix,rtn=" + barrix + "," + EncodingStreamReader.logInpChar(rtn));}
		//#endif
		return rtn;
	}

	int cr32leint(String mname, byte[] barr, int barrix) {
		//#ifdef DLOGGING
		if (logPcmpChar && traceLoggable) {logger.trace(mname + " cr32leint barrix,rtn=" + barrix + "," + EncodingStreamReader.logBarr(barr, barrix) + "," + EncodingStreamReader.logBarr(barr, barrix + 1));}
		//#endif
		int inputCharacter = 0;
		barrix += 3;
		for (int i = 3; i >= 0; i--) { 
			inputCharacter <<= 8;
			inputCharacter |= (int)barr[barrix--];
		}
		//#ifdef DLOGGING
		int rtn =
			//#else
			return
			//#endif
			inputCharacter;
		//#ifdef DLOGGING
		if (logCmpChar && traceLoggable) {logger.trace(mname + " cr32leint barrix,rtn=" + barrix + "," + EncodingStreamReader.logInpChar(rtn));}
		//#endif
		return rtn;
	}

	int cr16beint(String mname, byte[] barr, int barrix) {
		//#ifdef DLOGGING
		if (logPcmpChar && traceLoggable) {logger.trace(mname + " cr16beint barrix,rtn=" + barrix + "," + EncodingStreamReader.logBarr(barr, barrix) + "," + EncodingStreamReader.logBarr(barr, barrix + 1));}
		//#endif
		//#ifdef DLOGGING
		int rtn =
		//#else
		return
		//#endif
			((((int)barr[barrix] & 0xff) << 8) |
			((int)barr[barrix + 1] & 0xff));
		//#ifdef DLOGGING
		if (logCmpChar && traceLoggable) {logger.trace(mname + " cr16beint barrix,rtn=" + barrix + "," + EncodingStreamReader.logInpChar(rtn));}
		//#endif
		return rtn;
	}

	int cr16leint(String mname, byte[] barr, int barrix) {
		//#ifdef DLOGGING
		if (logPcmpChar && traceLoggable) {logger.trace(mname + " cr16leint barrix,rtn=" + barrix + "," + EncodingStreamReader.logBarr(barr, barrix) + "," + EncodingStreamReader.logBarr(barr, barrix + 1));}
		//#endif
		//#ifdef DLOGGING
		int rtn =
		//#else
		return
		//#endif
			((((int)barr[barrix + 1] & 0xff) << 8) |
			((int)barr[barrix] & 0xff));
		//#ifdef DLOGGING
		if (logCmpChar && traceLoggable) {logger.trace(mname + " cr16leint barrix,rtn=" + barrix + "," + EncodingStreamReader.logInpChar(rtn));}
		//#endif
		return rtn;
	}

	int cr8int(String mname, byte[] barr, int barrix) {
		//#ifdef DLOGGING
		if (logPcmpChar && traceLoggable) {logger.trace(mname + " cr8int barrix,rtn=" + barrix + "," + EncodingStreamReader.logBarr(barr, barrix));}
		//#endif
		//#ifdef DLOGGING
		int rtn =
		//#else
		return
		//#endif
			(int)barr[barrix] & 0xff;
		//#ifdef DLOGGING
		if (logCmpChar && traceLoggable) {logger.trace(mname + " cr8int barrix,rtn=" + barrix + "," + EncodingStreamReader.logInpChar(rtn));}
		//#endif
		return rtn;
	}

	ByteArrayOutputStream crbos(String mname, byte[] barr) {
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		cbos.write(barr, 0, barr.length);
		return cbos;
	}

	ByteArrayOutputStream cr16bebos(String mname, byte[] barr) {
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < barr.length; i++) {
			if ((int)barr[i] > 0x0000ff) {
				cbos.write((byte)(((int)barr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)barr[i]  & 0x000000ff));
			} else {
				cbos.write((byte)0);
				cbos.write((byte)((int)barr[i]  & 0x000000ff));
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr16lebos(String mname, StringBuffer sbuff) {
		char[] carr = sbuff.toString().toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)((int)carr[i] &  0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
			} else {
				cbos.write((byte)((int)carr[i] & 0x000000ff));
				cbos.write((byte)0);
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr16lebos(String mname, Object sbuff) {
		char[] carr = (sbuff instanceof StringBuffer) ?
			((StringBuffer)sbuff).toString().toCharArray() :
			((String)sbuff).toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)((int)carr[i] &  0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
			} else {
				cbos.write((byte)((int)carr[i] & 0x000000ff));
				cbos.write((byte)0);
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr16lebos(String mname, byte[] barr) {
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < barr.length; i++) {
			cbos.write(barr[i]);
			cbos.write((byte)0);
		}
		return cbos;
	}

	ByteArrayOutputStream crbos(String mname, StringBuffer sbuff) {
		char[] carr = sbuff.toString().toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			cbos.write((byte)carr[i]);
		}
		return cbos;
	}

	ByteArrayOutputStream crbos(String mname, Object sbuff) {
		char[] carr = (sbuff instanceof StringBuffer) ?
			((StringBuffer)sbuff).toString().toCharArray() :
			((String)sbuff).toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			cbos.write((byte)carr[i]);
		}
		return cbos;
	}

	ByteArrayOutputStream cr16bebos(String mname, StringBuffer sbuff) {
		char[] carr = sbuff.toString().toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		//#ifdef DLOGGING
		byte obyte1 = 0;
		byte obyte2 = 0;
		//#endif
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0x0000ff) {
				cbos.write(
				//#ifdef DLOGGING
				obyte1 =
				//#endif
						(byte)(((int)carr[i] & 0x0000ff00) >>> 8)
						);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else {
				cbos.write((byte)0);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			}
			//#ifdef DLOGGING
			if (logConvChar && traceLoggable) {logger.trace(mname + " cr16bebos i,obyte1,obyte2=" + i + "," + EncodingStreamReader.logByte(obyte1) + "," + EncodingStreamReader.logByte(obyte2));}
			//#endif
		}
		return cbos;
	}

	ByteArrayOutputStream cr16bebos(String mname, Object sbuff) {
		char[] carr = (sbuff instanceof StringBuffer) ?
			((StringBuffer)sbuff).toString().toCharArray() :
			((String)sbuff).toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		//#ifdef DLOGGING
		byte obyte1 = 0;
		byte obyte2 = 0;
		//#endif
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0x0000ff) {
				cbos.write(
				//#ifdef DLOGGING
				obyte1 =
				//#endif
						(byte)(((int)carr[i] & 0x0000ff00) >>> 8)
						);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else {
				cbos.write((byte)0);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			}
			//#ifdef DLOGGING
			if (logConvChar && traceLoggable) {logger.trace(mname + " cr16bebos i,obyte1,obyte2=" + i + "," + EncodingStreamReader.logByte(obyte1) + "," + EncodingStreamReader.logByte(obyte2));}
			//#endif
		}
		return cbos;
	}

	ByteArrayOutputStream cr32lebos(String mname, byte[] barr) {
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < barr.length; i++) {
			cbos.write(barr[i]);
			cbos.write((byte)0);
			cbos.write((byte)0);
			cbos.write((byte)0);
		}
		return cbos;
	}

	ByteArrayOutputStream cr32bebos(String mname, StringBuffer sbuff) {
		char[] carr = sbuff.toString().toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0xffffff) {
				cbos.write((byte)(((int)carr[i] & 0xff000000) >>> 16));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else if ((int)carr[i] > 0x00ffff) {
				cbos.write((byte)0);
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else {
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr32bebos(String mname, Object sbuff) {
		char[] carr = (sbuff instanceof StringBuffer) ?
			((StringBuffer)sbuff).toString().toCharArray() :
			((String)sbuff).toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0xffffff) {
				cbos.write((byte)(((int)carr[i] & 0xff000000) >>> 16));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else if ((int)carr[i] > 0x00ffff) {
				cbos.write((byte)0);
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			} else {
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr32lebos(String mname, StringBuffer sbuff) {
		char[] carr = sbuff.toString().toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0xffffff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0xff000000) >>> 16));
			} else if ((int)carr[i] > 0x00ffff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)0);
			} else if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)0);
				cbos.write((byte)0);
			} else {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)0);
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr32lebos(String mname, Object sbuff) {
		char[] carr = (sbuff instanceof StringBuffer) ?
			((StringBuffer)sbuff).toString().toCharArray() :
			((String)sbuff).toCharArray();
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < carr.length; i++) {
			if ((int)carr[i] > 0xffffff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)(((int)carr[i] & 0xff000000) >>> 16));
			} else if ((int)carr[i] > 0x00ffff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)(((int)carr[i] & 0x00ff0000) >>> 12));
				cbos.write((byte)0);
			} else if ((int)carr[i] > 0x0000ff) {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)(((int)carr[i] & 0x0000ff00) >>> 8));
				cbos.write((byte)0);
				cbos.write((byte)0);
			} else {
				cbos.write((byte)((int)carr[i]  & 0x000000ff));
				cbos.write((byte)0);
				cbos.write((byte)0);
				cbos.write((byte)0);
			}
		}
		return cbos;
	}

	ByteArrayOutputStream cr32bebos(String mname, byte[] barr) {
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		for (int i = 0; i < barr.length; i++) {
			cbos.write((byte)0);
			cbos.write((byte)0);
			cbos.write((byte)0);
			cbos.write(barr[i]);
		}
		return cbos;
	}

	void wrbos(ByteArrayOutputStream bos, ByteArrayOutputStream outbos) {
		byte[] barr = outbos.toByteArray();
		bos.write(barr, 0, barr.length);
	}

}
//#endif
//#endif
