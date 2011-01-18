//--Need to modify--#preprocess
/*
 * EncodingStreamReader.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * http://www.substanceofcode.com
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
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-01 1.11.5Dev15 Have set/getBitNbrDoc to keep track of how many bits make up a character.
 * IB 2011-01-01 1.11.5Dev15 In EncodingUtil, initialize formally static vars created by internal functions in getInstance to help reduce problems with not being able to save data.  Put all such vars in one block.  Make all methods non-static except getInstance and getEncodingUtil.  Move m_sglStatExcs to be first as it is depended upon by many vars.  Move vars with no or only m_sglStatExcs to follow init of m_sglStatExcs.  Move vars set by methods depending on other initialized vars to be second:  Only m_sglConvCp1252 was like this.  Have m_sglStatExcs be set to null after all initializations if it has no entries.  Remove code creating m_sglStatExcs from methods except for getStatExcs.  Have all methods ued for initialization have try/catch Throwable blocks to reduce initialization problems.
 * IB 2011-01-01 1.11.5Dev15 Do modification of the bytes if 16 or 32 bits.  Handle BE and LE for both.
 * IB 2011-01-01 1.11.5Dev15 Handle UTF-1 and UTF-7 partially assuming it will be supported by the device.
 * IB 2011-01-01 1.11.5Dev15 Do buffering so that we can read a few characters ahead and not make as many calls to super.read() to look at BOM and multi-byte characters.
 * IB 2011-01-01 1.11.5Dev15 Use only EncodingStreamReader to read data instead of having a separate InputStreamReader to read with.
 * IB 2011-01-01 1.11.5Dev15 Use modfication of bytes only in read(...) as read() is a special case of read(...) reading only one character.
 * IB 2011-01-01 1.11.5Dev15 Buffer reading of characters in XmlParser to reduce multiple calls to read().  Use new read(...) in EncodingStreamReader instead.
 * IB 2011-01-01 1.11.5Dev15 Handle BOM for UTF-8.
 * IB 2011-01-01 1.11.5Dev15 Better logging.
 * IB 2010-01-01 1.11.5Dev15 Have html flag to tell XmlParser and EncodingStreamReader to only allow HTMLParser to set the encoding except for byte order mark (BOM).
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use getEncodingUtil and getEncodingStreamReader to create EncodingUtil and EncodingStreamReader respectively to eliminate cross referencing in constructors.
 */

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define testing define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJMTEST
	//#define DNEEDLOG
//#elifdef DLOGGING
	//#define DNEEDLOG
//#endif
//#ifdef DFULLVERS
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Math;

import com.substanceofcode.utils.EncodingUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
public class EncodingStreamReader extends InputStreamReader {
    
    private String m_fileEncoding = "ISO8859_1";  // See below
    volatile private boolean m_eof = false;         // Indicates eof.
    volatile private boolean m_modEncoding = true;  // First mod encoding to account for
	                                    // unexpected big5 or UTF-16.
    volatile private boolean m_modBit16o32 = false;  // First mod encoding to account for 16 or 32 bit.
    volatile private byte m_bitNbrDoc = (byte)0; // How many bits in the doc.
    volatile private boolean m_utfDoc = false; // definately utf-1, utf-8, utf-16, or utf-32 doc
    volatile private boolean m_docBigEndian = true;  // Doc big endian
    volatile private boolean m_getPrologue = true;
    volatile private boolean m_getBom = true;    // Get the Byte Order Mark
    volatile private boolean m_firstChar = true; // 1st of a possible 2 byte character
    volatile private boolean m_secondChar = false; // 2nd of a possible 2 byte character
    private final int        MAX_READ_BUF = 10; // 2nd of a possible 2 byte character
    volatile private char[] m_rd; // Preread characters.
    volatile private int m_rdoff; // Preread characters.
    volatile private int m_rdlen; // Preread characters.
    private EncodingUtil m_encodingUtil; // Encoding util used for some definitions.
    private EncodingUtil m_encodingInstance; // Encoding util used for some definitions.
	//#ifdef DLOGGING
    private Logger m_logger;
    private boolean m_fineLoggable;
    private boolean m_finestLoggable;
    private boolean m_traceLoggable;
    volatile protected boolean m_logChar; // Log characters use traceLoggable
	//#endif
    
    /** Creates a new instance of EncodingStreamReader */
    private EncodingStreamReader(InputStream inputStream) {
		super(inputStream);
		m_fileEncoding = "";
		init();
		//#ifdef DLOGGING
		initLogging();
		if (m_finestLoggable) {m_logger.finest("Constructor inputStream=" + inputStream);}
		//#endif
	}

    private EncodingStreamReader(InputStream inputStream, String encoding)
	throws UnsupportedEncodingException {
		super(inputStream, encoding);
		m_fileEncoding = encoding;
		init();
		//#ifdef DLOGGING
		initLogging();
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("Constructor inputStream,encoding=" + inputStream + "," + encoding);}
		//#endif
		//#endif
	}

	private void init() {
		m_rd = new char[5 * MAX_READ_BUF];
		m_rdoff = 0;
		m_rdlen = 0;
	}

	//#ifdef DLOGGING
	private void initLogging() {
		m_logger = Logger.getLogger("EncodingStreamReader");
		m_fineLoggable = m_logger.isLoggable(Level.FINE);
		m_finestLoggable = m_logger.isLoggable(Level.FINEST);
		m_traceLoggable = m_logger.isLoggable(Level.TRACE);
		m_logChar    = m_traceLoggable; // Log characters use traceLoggable
	}
	//#endif

	static public EncodingStreamReader getEncodingStreamReader(
			InputStream inputStream, EncodingUtil encodingUtil) {
		EncodingUtil encodingInstance = encodingUtil.getQuickInstance();
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("EncodingStreamReader");
		//#endif
		// Open with as this will allow us to read the bytes from any
		// encoding., then after we get the prologue, convert the strings
		// with new String(...,encoding);
		EncodingStreamReader encStream = null;
		try {
			encStream = new EncodingStreamReader(inputStream,
					encodingInstance.getIsoEncoding());
		} catch (UnsupportedEncodingException e) {
			//#ifdef DLOGGING
			logger.severe("init read Could not open stream with encoding " +
						  encodingInstance.getIsoEncoding());
			//#endif
			System.out.println("init read Could not open stream with " +
					           "encoding " + encodingInstance.getIsoEncoding() +
							   " " + e + " " + e.getMessage());
			try {
				encStream = new EncodingStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
				//#ifdef DLOGGING
				logger.severe("init read Could not open stream with " +
						"encoding UTF-8");
				//#endif
				System.out.println("init read Could not open stream with " +
						"encoding UTF-8 " + e2 + " " + e2.getMessage());
				encStream = new EncodingStreamReader(inputStream);
			}
		}
		encStream.m_encodingUtil = encodingUtil;
		encStream.m_encodingInstance = encodingInstance;
		return encStream;
	}

	/**
	  Read the next character.  For UTF-16, read two bytes when
	  we discover this.
	  */
	public int read() throws IOException {
		try {
			//#ifdef DLOGGING
			int inputCharacter;
			//#endif
			char [] rcbuf = new char[1];
			int len;
			if ((len = read(rcbuf, 0, 1)) == -1) {
				//#ifdef DLOGGING
				inputCharacter =
					//#else
					return
					//#endif
					-1;
			} else {
				//#ifdef DLOGGING
				inputCharacter =
					//#else
					return
					//#endif
					(int)rcbuf[0];
			}
			//#ifdef DLOGGING
			if (m_logChar && m_traceLoggable) {m_logger.trace("read() inputCharacter=" + logInpChar(inputCharacter));}
			return inputCharacter;
			//#endif
		} catch (IOException e) {
			//#ifdef DLOGGING
			m_logger.severe("read() Could not read a char io error." + e, e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	private int[] readSave(int len)
	throws IOException {
		//#ifdef DLOGGING
		if (m_logChar && m_traceLoggable) {m_logger.trace("readSave entry m_rd[m_rdoff],m_rd[m_rdoff + 1],m_rdoff,m_rdlen,len=" + logcarr(m_rd, m_rdoff, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + len);}
		//#endif
		int olen = len;
		int inputCharacter = -2;
		if (m_rdlen > 0) {
			inputCharacter = (int)m_rd[m_rdoff++];
			if (--m_rdlen == 0) {
				m_rdoff = 0;
			}
			if (--len <= m_rdlen) {
				len = 0;
			} else {
				len -= m_rdlen;
			}
			//#ifdef DLOGGING
			if (m_logChar && m_traceLoggable) {m_logger.trace("readSave m_rdlen > 0 inputCharacter,m_rd[m_rdoff],m_rdoff,m_rdlen,len=" + logInpChar(inputCharacter) + "," + logcarr(m_rd, m_rdoff, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + len);}
			//#endif
		} else if (m_eof) {
			inputCharacter = -1;
			m_rdoff = 0;
			m_rdlen = 0;
			len = 0;
			//#ifdef DLOGGING
			if (m_logChar && m_traceLoggable) {m_logger.trace("readSave m_eof true inputCharacter,m_rd[m_rdoff],m_rdoff,m_rdlen,len=" + logInpChar(inputCharacter) + "," + logcarr(m_rd, m_rdoff, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + len);}
			//#endif
		}
		if (len > 0) {
			// If we get here, we have exausted m_rd.
			int mlen;
			if ((mlen = super.read(m_rd, m_rdoff + m_rdlen, len)) > 0) {
				if (len == olen) {
					inputCharacter = m_rd[m_rdoff++];
					m_rdlen += --mlen;
				} else {
					m_rdlen += mlen;
				}
				if (m_rdlen == 0) {
					m_rdoff = 0;
				}
			} else {
				if (len == olen) {
					inputCharacter = -1;
				}
				m_eof = true;
			}
		} else if (len == 1) {
			if (len == olen) {
				inputCharacter = super.read();
				m_rdoff = 0;
				m_rdlen = 0;
			} else {
				int endChar;
				if ((endChar = super.read()) != -1) {
					m_rd[m_rdoff + m_rdlen++] = (char)endChar;
				} else {
					m_eof = true;
				}
			}
			if (inputCharacter == -1) {
				m_eof = true;
			}
		} else {
			if (len == olen) {
				inputCharacter = -1;
			}
		}
		int nextCharacter = (m_rdlen > 0) ? m_rd[m_rdoff] : -2;
		//#ifdef DLOGGING
		if (m_logChar && m_traceLoggable) {m_logger.trace("readSave inputCharacter,nextCharacter,m_eof,m_rd[m_rdoff + 1],m_rd[m_rdoff + 2],m_rdoff,m_rdlen=" + logInpChar(inputCharacter) + "," + logInpChar(nextCharacter) + "," + m_eof + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen);}
		//#endif
		return new int[] {inputCharacter, nextCharacter};
	}

	private int[] peekNextchrs(int nlen)
	throws IOException {
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("peekNextchrs nlen,m_rd[m_rdoff],m_rdlen=" + nlen + "," + logcarr(m_rd, m_rdoff, m_rdlen) + "," + m_rdlen);}
		//#endif
		int tlen;
		int nextChar;
		if (m_rdlen > 0) {
			nextChar = (int)m_rd[m_rdoff];
			if (m_rdlen >= nlen) {
				tlen = nlen;
				nlen = 0;
			} else {
				tlen = m_rdlen;
				nlen -= m_rdlen;
			}
		} else {
			nextChar = -1;
			tlen = 0;
		}

		if (nlen > 0) {
			if (m_eof) {
				if (tlen == 0) {
					nextChar = -1;
				}
			} else {
				int mlen;
				if ((mlen = super.read(m_rd, m_rdoff + m_rdlen,
							nlen)) > 0) {
					nextChar = m_rd[m_rdoff];
					m_rdlen += mlen;
					tlen += mlen;
				} else if (mlen == -1) {
					if (m_rdlen == 0) {
						nextChar = -1;
					}
					m_eof = true;
				}
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("peekNextchrs after read(...) nextChar,nlen,mlen,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,m_eof=" + logInpChar(nextChar) + "," + nlen + "," + mlen + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + m_eof);}
				//#endif
			}
		}
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("peekNextchrs nextChar,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,tlen=" + logInpChar(nextChar) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + tlen);}
		//#endif
		return new int[] {nextChar, tlen};
	}

	private int getInpc(int nix, int noff)
	throws IOException {
		//#ifdef DLOGGING
		if (m_logChar && m_traceLoggable) {m_logger.trace("getInpc nix,noff,m_rd[0+],m_rd[+nix],m_rdoff,m_rdlen=" + nix + "," + noff + "," + logcarr(m_rd, m_rdoff, m_rdlen) + "," + logcarr(m_rd, m_rdoff + nix, m_rdlen) + "," + m_rdoff + "," + m_rdlen);}
		//#endif
		int inputCharacter;
		if (m_rdlen > 0) {
			if (nix < m_rdlen) {
				inputCharacter = m_rd[m_rdoff + nix];
				m_rdoff += noff;
				m_rdlen -= noff;
			} else {
				int mlen;
				int mdiff;
				if ((mlen = super.read(m_rd, m_rdoff + m_rdlen,
								(mdiff = (nix + 1 - m_rdlen)))) == -1) {
					m_eof = true;
					inputCharacter = -1;
					m_rdlen = 0;
					m_rdoff = 0;
				} else if (mlen < mdiff) {
					m_eof = true;
					inputCharacter = -1;
					m_rdlen = 0;
					m_rdoff = 0;
				} else {
					inputCharacter = m_rd[m_rdoff + nix];
					m_rdlen = 1 - noff + nix;
					m_rdoff = noff - nix;
				}
				//#ifdef DLOGGING
				if (m_logChar && m_traceLoggable) {m_logger.trace("getInpc after read(...) inputCharacter,mlen,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,m_eof=" + logInpChar(inputCharacter) + "," + mlen + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + m_eof);}
				//#endif
			}
		} else if (m_eof) {
			inputCharacter = -1;
			m_rdoff = 0;
			m_rdlen = 0;
		} else {
			m_rdlen = 0;
			m_rdoff = 0;
			// Future int[] nres;
			// Future ((inputCharacter = (nres = readSave(1))[0]) == -1)
			if ((inputCharacter = super.read()) == -1) {
				m_eof = true;
			} else {
				if (noff <= nix) {
					m_rdlen = 1;
				}
			}
		}
		//#ifdef DLOGGING
		if (m_logChar && m_traceLoggable) {m_logger.trace("getInpc inputCharacter,noff,m_rd[0],m_rd[1],m_rd[1],m_rdoff,m_rdlen=" + logInpChar(inputCharacter) + "," + logcarr(m_rd, m_rdoff, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 3, m_rdlen) + "," + m_rdoff + "," + m_rdlen);}
		//#endif
		return inputCharacter;
	}

	private void setEofNext() {
		if ((m_rdoff + m_rdlen + (2 * MAX_READ_BUF)) >= m_rd.length) {
			System.arraycopy(m_rd, m_rdoff, m_rd, 0, m_rdlen);
			m_rdoff = 0;
			//#ifdef DLOGGING
			if (m_logChar && m_traceLoggable) {m_logger.trace("setEofNext m_rd[0],m_rd[1],m_rd[2],m_rd[3],m_rdlen=" + logcarr(m_rd, 0, m_rdlen) + "," + logcarr(m_rd, 1, m_rdlen) + "," + logcarr(m_rd, 2, m_rdlen) + "," + logcarr(m_rd, 3, m_rdlen) + "," + m_rdlen);}
			//#endif
		//#ifdef DLOGGING
		} else {
			if (m_logChar && m_traceLoggable) {m_logger.trace("setEofNext m_rd not changed m_rd[+0],m_rd[+1],m_rd[+2],m_rd[+3],m_rdlen=" + logcarr(m_rd, m_rdoff, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 3, m_rdlen) + "," + m_rdlen);}
		//#endif
		}
	}

	static public boolean cmpSubstr2c(char[] carr, int coff, int clen,
			char[] cmparr
			//#ifdef DLOGGING
			,boolean logChar, boolean traceLoggable, Logger logger
			//#endif
			) {
		boolean res = true;
		int cmpoff = 0;
		while ((cmpoff < cmparr.length) && (cmpoff < clen)) {
			if (carr[coff++] != cmparr[cmpoff++]) {
				//#ifdef DLOGGING
				if (logChar && traceLoggable) {logger.trace("cmpSubstr2c unequal carr[coff - 1],cmparr[cmpoff - 1]=" + logcarr(carr, coff - 1, 1) + "," + logcarr(cmparr, cmpoff - 1, 1));}
				//#endif
				res = false;
				break;
			}
		}
		if (res) {
			res = (cmpoff == cmparr.length);
		}
		//#ifdef DLOGGING
		if (logChar && traceLoggable) {logger.trace("cmpSubstr2c res,coff,cmpoff,clen=" + res + "," + coff + "," + cmpoff + "," + clen);}
		//#endif
		return res;
	}

	public int read(char [] cbuf, int off, int len) throws IOException {
		int n = 0;
		try {
			//#ifdef DLOGGING
			if (m_logChar && (m_rdlen > 0) && m_traceLoggable) {m_logger.trace("read(...) begin m_rd m_rd[m_rdoff],m_rd[m_rdoff + 1],m_rd[m_rdoff + 2],m_rd[m_rdoff + 3],m_rdoff,m_rdlen=" + logcarr(m_rd, m_rdoff, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 3, m_rdlen) + "," + m_rdoff + "," + m_rdlen);}
			//#endif
			if (m_modEncoding) {
				if (len == 0) {
					return 0;
				}
				int inputCharacter = -1;
				try {
					while (len > 0) {
						int[] nres;
						inputCharacter = (nres = readSave(m_getBom ?
									(2 * MAX_READ_BUF) : MAX_READ_BUF))[0];
						int nextCharacter = nres[1];
						int tlen = m_rdlen;
						boolean readNext = false;
						//#ifdef DLOGGING
						if (m_logChar && m_traceLoggable) {m_logger.trace("read(...) inputCharacter,m_modEncoding,m_getPrologue,m_modBit16o32,m_docBigEndian,m_firstChar,m_secondChar=" + logInpChar(inputCharacter) + "," + m_modEncoding + "," + m_getPrologue + "," + m_modBit16o32 + "," + m_docBigEndian + "," + m_firstChar + "," + m_secondChar);}
						//#endif
						if (inputCharacter == -1) {
							m_eof = true;
							break;
						}
						boolean needPeek = false;
						if (m_modEncoding && (m_getPrologue ||
									m_modBit16o32)) {
							// If we get 0 character during prologue, it must be
							// first or second byte of UTF-16.  Throw it out.
							// If little endian, we need to read the next character.
							if (m_secondChar) {
								// If 0 character, it is UTF-16 and little endian.
								if (inputCharacter == 0) {
									int nextix;
									int nextoff;
									int[] prevChars = null;
									if ((tlen >= 3) &&
											(nextCharacter == (char)0) &&
											(m_rd[m_rdoff + 1] == (char)0)) {
										m_bitNbrDoc = m_encodingInstance.UTF_32;
										m_modBit16o32 = true;
										m_docBigEndian = false;
										m_utfDoc = true;
										nextix = 2;
										nextoff = 3;
									} else if (tlen >= 1) {
										m_bitNbrDoc = m_encodingInstance.UTF_16;
										m_utfDoc = true;
										m_modBit16o32 = true;
										m_docBigEndian = false;
										nextix = 0;
										nextoff = 1;
									} else {
										nextix = -1;
										nextoff = -1;
									}
									if ((tlen >= 1) && (nextoff >= 0)) {
										needPeek = true;
										if ((inputCharacter = getInpc(nextix,
														nextoff)) == -1) {
											break;
										}
									}
								}
								m_firstChar = true;
								m_secondChar = false;
							}
							if (m_firstChar) {
								if (m_getBom) {
									m_getBom = false;
									//#ifdef DLOGGING
									if (m_traceLoggable) {m_logger.trace("read(...) inputCharacter=" + "," + logInpChar(inputCharacter));}
									//#endif
									//#ifdef DLOGGING
									try {
										//#endif
										int flen;
										if ((inputCharacter == 0x00) ||
												(inputCharacter == 0x2b) ||
												(inputCharacter == 0xef) ||
												(inputCharacter == 0xff) ||
												(inputCharacter == 0xf7) ||
												(inputCharacter == 0xfe)) {
											if (needPeek) {
												nextCharacter = (nres =
														peekNextchrs(
															8))[0];
												tlen = nres[1];
											}
											flen = tlen;
										} else {
											flen = 0;
										}
										//#ifdef DLOGGING
										if (m_traceLoggable) {m_logger.trace("read(...) inputCharacter,tlen,flen,m_rdoff,m_rdlen=" + "," + logInpChar(inputCharacter) + "," + tlen + "," + flen + "," + m_rdoff + "," + m_rdlen);}
										//#endif
										int nextix = -1;
										int nextoff = -1;
										//#ifdef DLOGGING
										if (m_traceLoggable) {m_logger.trace("read(...) read bom switch inputCharacter,nextCharacter=" + logInpChar(inputCharacter) + "," + logInpChar(nextCharacter));}
										//#endif
										if (flen >= 2) {
											boolean modEncoding = m_modEncoding;
											switch (inputCharacter) {
												case 0xff:
													if (nextCharacter ==
															0xfe) {
														if ((tlen >= 4) &&
																cmpSubstr2c(
																	m_rd,
																	m_rdoff + 1,
																	m_rdlen - 1,
																	new char[] {
																	(char)0, (char)0}
																	//#ifdef DLOGGING
																	,m_logChar
																	,m_traceLoggable
																	,m_logger
																	//#endif
																	)) {
															m_bitNbrDoc = m_encodingInstance.UTF_32;
															nextix = 3;
															nextoff = 4;
														} else {
															m_bitNbrDoc =
																m_encodingInstance.UTF_16;
															nextix = 1;
															nextoff = 2;
														}
														m_modBit16o32 = true;
														m_docBigEndian = false;
														m_utfDoc = true;
														readNext = true;
													}
													break;
												case 0xfe:
													if (nextCharacter ==
															0xff) {
														m_modBit16o32 = true;
														m_bitNbrDoc = m_encodingInstance.UTF_16;
														m_docBigEndian = true;
														m_utfDoc = true;
														nextix = 1;
														nextoff = 2;
														readNext = true;
													}
													break;
												case 0x00:
													if ((nextCharacter ==
																0x00) &&
															(tlen >= 4) &&
															cmpSubstr2c(
																m_rd,
																m_rdoff + 1,
																m_rdlen - 1,
																new char[]
																{(char)0xfe,
																(char)0xff}
																//#ifdef DLOGGING
																,m_logChar
																,m_traceLoggable
																,m_logger
																//#endif
																)) {
														m_bitNbrDoc = m_encodingInstance.UTF_32;
														m_modBit16o32 = true;
														m_docBigEndian = true;
														m_utfDoc = true;
														readNext = true;
														nextix = 3;
														nextoff = 4;
													}
													break;
												case 0xef:
													if ((tlen >= 3) &&
															cmpSubstr2c(
																m_rd,
																m_rdoff,
																m_rdlen,
																new char[] {(char)0xbb,
																(char)0xbf}
																//#ifdef DLOGGING
																,m_logChar
																,m_traceLoggable
																,m_logger
																//#endif
																)) {
														m_bitNbrDoc = m_encodingInstance.UTF_8;
														m_modBit16o32 = false;
														m_docBigEndian = true;
														m_utfDoc = true;
														modEncoding = false;
														readNext = true;
														nextix = 2;
														nextoff = 3;
													}
													break;
												case 0x2b:
													if ((tlen >= 4) &&
															cmpSubstr2c(
																m_rd,
																m_rdoff, m_rdlen,
																new char[] {(char)0x2f,
																(char)0x76}
																//#ifdef DLOGGING
																,m_logChar
																,m_traceLoggable
																,m_logger
																//#endif
																)) {
														char lastc = m_rd[m_rdoff +
															2];
														if ((lastc == 0x38) ||
																(lastc == 0x39) ||
																(lastc == 0x2b) ||
																(lastc == 0x2f)) {
															m_bitNbrDoc = m_encodingInstance.UTF_7;
															m_modBit16o32 = false;
															modEncoding = false;
															m_docBigEndian = true;
															m_utfDoc = true;
															readNext = true;
															nextix = 3;
															nextoff = 4;
														}
													}
													break;
												case 0xf7:
													if ((tlen >= 3) &&
															cmpSubstr2c(
																m_rd,
																m_rdoff, m_rdlen,
																new char[] {(char)0x64,
																(char)0x4c}
																//#ifdef DLOGGING
																,m_logChar
																,m_traceLoggable
																,m_logger
																//#endif
																)) {
														m_bitNbrDoc = m_encodingInstance.UTF_1;
														m_modBit16o32 = false;
														modEncoding = false;
														m_docBigEndian = true;
														m_utfDoc = true;
														readNext = true;
														nextix = 2;
														nextoff = 3;
													}
													break;
												default:
													break;
											}
											if (m_modEncoding != modEncoding) {
												setModEncoding(modEncoding);
											}
										}
										if (readNext) {
											needPeek = true;
											if ((inputCharacter = getInpc(
															nextix, nextoff))
													== -1) {
												break;
											}
										}
										//#ifdef DLOGGING
									} finally {
										if (m_traceLoggable) {m_logger.trace("read(...) inputCharacter,m_modBit16o32,m_docBigEndian,m_bitNbrDoc,m_utfDoc,m_modEncoding,m_eof=" + logInpChar(inputCharacter) + "," + m_modBit16o32 + "," + m_docBigEndian + ",<" + m_bitNbrDoc + "," + m_encodingUtil.getBitEncoding(m_bitNbrDoc) + ">," + m_utfDoc + "," + m_modEncoding + "," + m_eof);}
									}
									//#endif
									// If we already know that it is utf,
									// we need not look at second over
									// all character.
									m_secondChar = !m_utfDoc;
								}
								boolean procNext = true;
								if (!m_modBit16o32 && !m_utfDoc &&
										m_getPrologue) {
									if (inputCharacter == 0) {
										if (needPeek) {
											nextCharacter = (nres =
													peekNextchrs(
														4))[0];
											tlen = nres[1];
										}
										m_docBigEndian = true;
										int nextix = -1;
										int nextoff = -1;
										if ((tlen >= 3) &&
												(nextCharacter == (char)0) &&
												(m_rd[m_rdoff + 1] == (char)0)) {
											m_bitNbrDoc = m_encodingInstance.UTF_32;
											m_modBit16o32 = true;
											m_docBigEndian = true;
											m_utfDoc = true;
											nextix = 2;
											nextoff = 3;
											procNext = false;
										} else if (tlen >= 1) {
											m_bitNbrDoc = m_encodingInstance.UTF_16;
											m_modBit16o32 = true;
											m_docBigEndian = true;
											m_utfDoc = true;
											nextix = 0;
											nextoff = 1;
											procNext = false;
										}
										if ((tlen >= 1) &&
												(nextoff >= 0)){
											needPeek = true;
											if ((inputCharacter = getInpc(
															nextix, nextoff)) ==
													-1) {
												m_eof = true;
											}
										}
										//#ifdef DLOGGING
										if (m_traceLoggable) {m_logger.trace("after first byte is 0 inputCharacter,m_bitNbrDoc,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,tlen=" + logInpChar(inputCharacter) + ",<" + m_bitNbrDoc + "," + m_encodingUtil.getBitEncoding(m_bitNbrDoc) + ">," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + tlen);}
										//#endif
									}
								}
								if (m_modBit16o32 && procNext) {
									// If we know that this is UTF-16 from
									// encoding, put the bytes together correctly.
									// In this case, we are not in prolog, so
									// both bytes may be meaningful.
									int nextix = -1;
									int nextoff = -1;
									if (m_bitNbrDoc == m_encodingInstance.UTF_16) {
										if (needPeek) {
											if (m_rdlen >= 2) {
												nextCharacter = m_rd[m_rdoff];
												tlen = 2;
												//#ifdef DLOGGING
												if (m_traceLoggable) {m_logger.trace("before 16 bit skip peek nextCharacter,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,tlen=" + logInpChar(nextCharacter) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + tlen);}
												//#endif
											} else {
												nextCharacter = (nres =
														peekNextchrs(2))[0];
												tlen = nres[1];
											}
										}
										if ((nextCharacter >= 0) &&
												(tlen >= 1)) {
											if (m_docBigEndian) {
												inputCharacter <<= 8;
												inputCharacter |= nextCharacter;
											} else {
												nextCharacter <<= 8;
												nextCharacter |= inputCharacter;
												inputCharacter = nextCharacter;
											}
											nextix = 0;
											nextoff = 1;
											//#ifdef DLOGGING
											if (m_traceLoggable) {m_logger.trace("read(...) 16 bit inputCharacter,nextix,nextoff,len,m_eof=" + logInpChar(inputCharacter) + "," + nextix + "," + nextoff + "," + len + "," + m_eof);}
											//#endif
										}
									} else {
										// 32 bit chars
										if (needPeek) {
											if (m_rdlen >= 4) {
												nextCharacter = m_rd[m_rdoff];
												tlen = 4;
												//#ifdef DLOGGING
												if (m_traceLoggable) {m_logger.trace("before 32 bit skip peek nextCharacter,m_rd[+1],m_rd[+2],m_rdoff,m_rdlen,tlen=" + logInpChar(nextCharacter) + "," + logcarr(m_rd, m_rdoff + 1, m_rdlen) + "," + logcarr(m_rd, m_rdoff + 2, m_rdlen) + "," + m_rdoff + "," + m_rdlen + "," + tlen);}
												//#endif
											} else {
												nextCharacter = (nres =
														peekNextchrs(4))[0];
												tlen = nres[1];
											}
										}
										if (tlen >= 3) {
											if (m_docBigEndian) {
												int ix = m_rdoff;
												for (int i = 1; i <= 3; i++) { 
													inputCharacter <<= 8;
													inputCharacter |=
														(int)m_rd[ix++];
												}
											} else {
												int ix = m_rdoff + 2;
												for (int i = 1; i >= 0; i--) { 
													nextCharacter <<= 8;
													nextCharacter |=
														(int)m_rd[ix--];
												}
												nextCharacter <<= 8;
												nextCharacter |= inputCharacter;
												inputCharacter = nextCharacter;
											}
											nextix = 2;
											nextoff = 3;
											//#ifdef DLOGGING
											if (m_traceLoggable) {m_logger.trace("read(...) 32 bit inputCharacter,nextix,nextoff,len,m_eof=" + logInpChar(inputCharacter) + "," + nextix + "," + nextoff + "," + len + "," + m_eof);}
											//#endif
										}
									}
									if ((tlen >= 1) && (nextoff >= 0)) {
										int tmpChar = getInpc(nextix, nextoff);
										//#ifdef DLOGGING
									} else {
										if (m_traceLoggable) {m_logger.trace("read(...) after 16 or 32 bit m_bitNbrDoc,inputCharacter,nextix,nextoff,tlen,len,m_eof=<" + m_bitNbrDoc + "," + m_encodingUtil.getBitEncoding(m_bitNbrDoc) + ">," + logInpChar(inputCharacter) + "," + nextix + "," + nextoff + "," + tlen + "," + len + "," + m_eof);}
										//#endif
									}
								}
							}
						}
						if (inputCharacter == -1) {
							m_eof = true;
							break;
						} else {
							cbuf[off++] = (char)inputCharacter;
							n++;
							len--;
							//#ifdef DLOGGING
							if (m_traceLoggable) {m_logger.trace("read(...) off,inputCharacter,cbuf[off - 1],m_rd[m_rdoff],m_rdoff,m_rdlen,n=" + off + "," + logInpChar(inputCharacter) + "," + logcarr(cbuf, off - 1, 1) + "," + logcarr(m_rd, m_rdoff, 1) + "," + m_rdlen + "," + n);}
							//#endif
						}
						if (m_rdlen > 0) {
							setEofNext();
						}
					}
				} finally {
					//#ifdef DLOGGING
					if (m_finestLoggable && m_logChar) {m_logger.finest("read(...) finished read itch inputCharacter,n,off,cbuf[off - 1]=" + logInpChar(inputCharacter) + "," + n + "," + off + "," + logcarr(cbuf, off - 1, 1));}
					if (m_finestLoggable && m_logChar) {m_logger.finest("read(...) finished read itch m_rd[m_rdoff],m_rdoff,m_rdlen=" + logcarr(m_rd, m_rdoff, m_rdlen) + "," + m_rdoff + "," + m_rdlen);}
					//#endif
					if ((inputCharacter == -1) && (n == 0)) {
						n = -1;
					}
				}
			} else if (m_rdlen > 0) {
				int mlen = Math.min(m_rdlen, len);
				if (mlen == 1) {
					cbuf[off++] = m_rd[m_rdoff++];
					len--;
				} else {
					System.arraycopy(m_rd, m_rdoff, cbuf, off, mlen);
					off += mlen;
					m_rdoff -= mlen;
					len -= mlen;
				}
				m_rdlen -= mlen;
				n += mlen;
				//#ifdef DLOGGING
				if (m_logChar && m_traceLoggable) {m_logger.trace("read(...) get m_rd cbuf[off - mlen],cbuf[off - mlen + 1],off - mlen,mlen,off,len=" + logcarr(cbuf, off - mlen, mlen) + "," + logcarr(cbuf, off - mlen + 1, mlen) + "," + (off - mlen) + "," + mlen + "," + off + "," + len);}
				if (m_logChar && m_traceLoggable) {m_logger.trace("read(...) get m_rd m_rd[0],m_rd[1],m_rd[2],m_rdlen=" + logcarr(m_rd, 0, mlen) + "," + logcarr(m_rd, 1, mlen) + "," + logcarr(m_rd, 2, mlen) + "," + m_rdlen);}
				//#endif
			} else if (m_eof) {
				n = -1;
				//#ifdef DLOGGING
				if (m_traceLoggable) {m_logger.trace("read(...) finished read cbuf n,m_eof=" + n + "," + m_eof);}
				//#endif
				len = 0;
			}
			if (len > 0) {
				int clen;
				if ((clen = super.read(cbuf, off, len)) == -1) {
					m_eof = true;
					if (n == 0) {
						n = -1;
					}
				} else {
					n += clen;
				}
				//#ifdef DLOGGING
				if (m_traceLoggable) {m_logger.trace("read(...) finished read cbuf n,off,cbuf[off]=" + n + "," + off + "," + logcarr(cbuf, off, n));}
				//#endif
			}
			//#ifdef DLOGGING
			if (m_traceLoggable && m_logChar) {m_logger.trace("read(...) return n=" + n);}
			//#endif
		} catch (IOException e) {
//#ifdef DLOGGING
			m_logger.severe("read(...) Could not read a char io error.", e);
//#endif
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
//#ifdef DLOGGING
			m_logger.severe("read(...) Could not read a char run time.", e);
//#endif
			System.out.println("read(...) Could not read a char run time." + e + " " +
					           e.getMessage());
		}
		return n;
	}

    public void setModEncoding(boolean modEncoding) {
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setModEncoding modEncoding=" + modEncoding);}
		//#endif
        this.m_modEncoding = modEncoding;
    }

    public boolean isModEncoding() {
        return (m_modEncoding);
    }

    public void setFileEncoding(String fileEncoding) {
        this.m_fileEncoding = fileEncoding;
    }

    public String getFileEncoding() {
        return (m_fileEncoding);
    }

    public void setGetPrologue(boolean getPrologue) {
        this.m_getPrologue = getPrologue;
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setGetPrologue m_getPrologue=" + m_getPrologue);}
		//#endif
    }

    public boolean isGetPrologue() {
        return (m_getPrologue);
    }

    public void setModBit16o32(boolean modBit16o32) {
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setModBit16o32 modBit16o32=" + modBit16o32);}
		//#endif
        this.m_modBit16o32 = modBit16o32;
    }

    public boolean isModBit16o32() {
        return (m_modBit16o32);
    }

    public void setBitNbrDoc(byte bitNbrDoc) {
        this.m_bitNbrDoc = bitNbrDoc;
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setBitNbrDoc m_bitNbrDoc=" + m_bitNbrDoc);}
		//#endif
    }

    public byte getBitNbrDoc() {
        return (m_bitNbrDoc);
    }

    public void setUtfDoc(boolean utfDoc) {
        this.m_utfDoc = utfDoc;
    }

    public boolean isUtfDoc() {
        return (m_utfDoc);
    }

    public void setEncodingUtil(EncodingUtil encodingUtil) {
        this.m_encodingUtil = encodingUtil;
    }

    public EncodingUtil getEncodingUtil() {
        return (m_encodingUtil);
    }

	//#ifdef DLOGGING
	//#ifdef DTEST
    public void setLogChar(boolean logChar) {
        this.m_logChar = logChar;
    }

    public boolean isLogChar() {
        return (m_logChar);
    }
	//#endif
	//#endif

	//#ifdef DNEEDLOG
	public static String logInpChar(int inputCharacter) {
		try {
			return "<" + ((inputCharacter == -1) ? "-1" : ((char)inputCharacter + "," + inputCharacter + "," + Integer.toString(inputCharacter, 16))) + ">";
		} catch (RuntimeException e) {
			//#ifdef DLOGGING
			Logger.getLogger("EncodingStreamReader").severe("logInpChar Could not read a char io error." + e, e);
			//#endif
			throw e;
		}
	}

	static public String logByte(byte b) {
		return "<" + (char)((int)b & 0xff) + "," + b + "," + Integer.toString(((int)b & 0xff), 16) + ">";
	}

	static public String logBarr(byte[] barr, int ic) {
		try {
			if (barr == null) {
				return "<null>";
			} else if (ic >= barr.length) {
				return "<index out of bounds ic,barr.length=" + ic + "," + barr.length + ">";
			} else if (ic >= 0) {
				return "<" + ic + "," + (char)((int)barr[ic] & 0xff) + "," + ((int)barr[ic] & 0xff) + "," + Integer.toString(((int)barr[ic] & 0xff), 16) + ">";
			} else {
				return "<index out of bounds ic,barr.length=" + ic + "," + barr.length + ">";
			}
		} catch (RuntimeException e) {
			//#ifdef DLOGGING
			Logger.getLogger("EncodingStreamReader").severe("logcarr Could not read a char io error." + e, e);
			//#endif
			throw e;
		}
	}

	static public String logChar(char cch) {
		return "<" + cch + "," + (int)cch + "," + Integer.toString((int)cch, 16) + ">";
	}

	static public String logcarr(char[] carr, int ic, int len) {
		try {
			if (carr == null) {
				return "<null>";
			} else if (ic >= carr.length) {
				return "<index out of bounds ic,carr.length=" + ic + "," + carr.length + ">";
			} else if (len == 0) {
				return "<" + ic + ",len=0>";
			} else if (ic >= 0) {
				return "<" + ic + "," + carr[ic] + "," + (int)carr[ic] + "," + Integer.toString((int)carr[ic], 16) + ">";
			} else {
				return "<index out of bounds ic,carr.length=" + ic + "," + carr.length + ">";
			}
		} catch (RuntimeException e) {
			//#ifdef DLOGGING
			Logger.getLogger("EncodingStreamReader").severe("logcarr Could not read a char io error." + e, e);
			//#endif
			throw e;
		}
	}

	static public String logsoarr(Object[] soarr, int ic) {
		try {
			if (soarr == null) {
				return "<null>";
			} else if (ic >= soarr.length) {
				return "<index out of bounds ic,soarr.length=" + ic + "," + soarr.length + ">";
			} else if (ic >= 0) {
				return "<" + ic + "," + soarr[ic].toString() + ">";
			} else {
				return "<index out of bounds ic,soarr.length=" + ic + "," + soarr.length + ">";
			}
		} catch (RuntimeException e) {
			//#ifdef DLOGGING
			Logger.getLogger("EncodingStreamReader").severe("logsoarr Could not read a object io error." + e, e);
			//#endif
			throw e;
		}
	}

	static public String logiarr(int[] iarr, int ic) {
		try {
			if (iarr == null) {
				return "<null>";
			} else if (ic >= iarr.length) {
				return "<index out of bounds ic,iarr.length=" + ic + "," + iarr.length + ">";
			} else if (ic >= 0) {
				return "<" + ic + "," + (char)iarr[ic] + "," + iarr[ic] + "," + Integer.toString(iarr[ic], 16) + ">";
			} else {
				return "<index out of bounds ic,iarr.length=" + ic + "," + iarr.length + ">";
			}
		} catch (RuntimeException e) {
			//#ifdef DLOGGING
			Logger.getLogger("EncodingStreamReader").severe("logiarr Could not read a char io error." + e, e);
			//#endif
			throw e;
		}
	}

	//#endif

}
//#endif
