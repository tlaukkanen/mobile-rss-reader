//--Need to modify--#preprocess
/*
 * XmlParser.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007-2010 Irving Bunton, Jr
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
 * IB 2010-03-14 1.11.5RC2 Ignore comments to allow more XML to parse successfully.
 * IB 2010-04-04 1.11.5RC2 Don't insert '</' into element name.
 * IB 2010-04-04 1.11.5RC2 Fix for when close tag contains only part of begin tag followed by other characters .
 * IB 2010-04-25 1.11.5RC2 Fix to prevent string out of bounds.
 * IB 2010-04-30 1.11.5RC2 Fixed problem with end tags not recognized if spaces are inside.
 * IB 2010-04-30 1.11.5RC2 Free up memory from string when getting text.
 * IB 2010-04-30 1.11.5RC2 Recognize CDATA, style sheet, and DOCTYPE and treat properly.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-05-29 1.11.5RC2 Allow multiple meta statements.
 * IB 2010-05-29 1.11.5RC2 Reprocess PROLOGUE if we find it again.
 * IB 2010-07-04 1.11.5Dev6 Collapse nested if statements.
 * IB 2010-07-04 1.11.5Dev6 Cosmetic code cleanup.
 * IB 2010-07-04 1.11.5Dev6 Replace empty while with for.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-07-04 1.11.5Dev6 Code cleanup.
 * IB 2010-07-28 1.11.5Dev8 Don't convert entities if CDATA used or not forced.
 * IB 2010-09-27 1.11.5Dev8 Combine if statements.
 * IB 2010-09-27 1.11.5Dev8 Remove unneeded ;.
 * IB 2010-09-29 1.11.5Dev9 Convert & in attributes to &amp;.
 * IB 2010-09-29 1.11.5Dev9 Have convAttrData to convert &,<,> in attributes data to &amp;,&#60;,&#62;.
 * IB 2010-09-29 1.11.5Dev9 Have convAttrUrlData to convert &,<,> in attributes data to &amp;,%3C,%3E.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-01 1.11.5Dev15 Buffer reading of characters in XmlParser to reduce multiple calls to read().  Use new read(...) in EncodingStreamReader instead.
 * IB 2011-01-01 1.11.5Dev15 Use only EncodingStreamReader to read data instead of having a separate InputStreamReader to read with.
 * IB 2011-01-01 1.11.5Dev15 Handle BOM for UTF-8.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use getEncodingUtil and getEncodingStreamReader to create EncodingUtil and EncodingStreamReader respectively to eliminate cross referencing in constructors.
 * IB 2011-01-24 1.11.5Dev16 Put in comment out future code to use both local and global vars for buffering.
*/

// Expand to define full vers define
//#define DFULLVERS
// Expand to define full vers define
//#define DNOINTLINK
// Expand to define smartphone define
//#define DNOSMARTPHONE
// Expand to define testing define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DFULLVERS
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

//#ifdef DTEST
//@import com.substanceofcode.utils.EncodingUtilIntr;
//#endif

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
/**
 * Simple and lightweight XML parser without complete error handling.
 *
 * @author Tommi Laukkanen
 */
public class XmlParser {
    
    /** Enumerations for parse function */
    public static final int PARTIAL_TEXT = 0;
    public static final int UNKNOWN_ELEMENT = 1;
    public static final int END_DOCUMENT = 2;
    public static final int ELEMENT = 3;
    public static final int PROLOGUE = 4;
    public static final int STYLESHEET = 5;
    public static final int DOCTYPE = 6;
    public static final int CDATA = 7;
    public static final int COMMENT = 8;
    public static final int CLOSE_TAG = 9;
    public static final int LAST_TOKEN = CLOSE_TAG;
    private static final String BEGIN_PROLOGUE = "<?xml";
    private static final String END_PROLOGUE = "?>";
    private static final String BEGIN_STYLESHEET = "<?xml-stylesheet";
    private static final String END_STYLESHEET = "?>";
    private static final String BEGIN_CDATA = "<![CDATA[";
    private static final String END_CDATA = "]]>";
    private static final String BEGIN_DOCTYPE_REF = "<!DOCTYPE";
    private static final String END_DOCTYPE_REF = "\">";
    private static final String BEGIN_COMMENT = "<!--";
    private static final String END_COMMENT = "-->";
    private static final String BEGIN_CLOSE_TAG = "</";
    private static final String END_CLOSE_TAG = ">";
    private static final String BEGIN_META = "<meta";
    
	final       Object m_nullPtr = null;
	volatile int m_readLen;
	volatile int m_lenRead;
	volatile int m_offread;
	volatile boolean m_eof;
	boolean htmlFile;
	char[] m_cbuf;
    /** Current XML element name (eg. <title> = title) */
    final protected StringBuffer m_currentElementName = new StringBuffer();
    final protected StringBuffer m_currentElementData = new StringBuffer();
    protected boolean m_currentElementContainsText = false;
	//#ifdef DTEST
//@    boolean m_debugTrace = false;  // traceLoggable to add extra trace
	//#endif
    protected String m_fileEncoding = "ISO8859_1";  // See EncodingUtil
    protected String m_docEncoding = "";  // See EncodingUtil
    protected String m_defEncoding = "UTF-8";  // Default doc encoding
    protected EncodingUtil m_encodingUtil = null;
    final protected EncodingUtil m_encodingInstance;
    protected EncodingStreamReader m_encodingStreamReader;
    private String [] m_namespaces = null;
    private boolean m_getPrologue = true;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("XmlParser");
//@    final private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    final private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    final private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@    final private boolean traceLoggable = logger.isLoggable(Level.TRACE);
//@    volatile protected boolean m_logChar    = false; // Log characters use traceLoggable
//@    volatile protected boolean m_logRepeatChar = false; // Log characters use traceLoggable
//@
//@    volatile protected boolean m_logReadChar = false; // Log characters use traceLoggable
//@
	//#endif
    
    /** Creates a new instance of XmlParser */
    public XmlParser(InputStream inputStream) {
		this(EncodingUtil.getEncodingUtil(inputStream));
    }

    /** Creates a new instance of XmlParser */
    public XmlParser(EncodingUtil encodingUtil) {
		this.m_encodingUtil = encodingUtil;
		m_encodingStreamReader = m_encodingUtil.getEncodingStreamReader();
		m_fileEncoding = m_encodingStreamReader.getFileEncoding();
		// If smartphone, use more memory.
		//#ifdef DSMARTPHONE
//@		m_cbuf = new char[200];
		//#else
		m_cbuf = new char[100];
		//#endif
		m_readLen = m_cbuf.length;
		m_lenRead = m_cbuf.length;
		m_offread = m_cbuf.length;
		m_eof     = false;
		htmlFile  = false;
		m_encodingInstance = m_encodingUtil.getQuickInstance();
    }

  /**
   * Process the prologue.  Set encoding.
   *
   * @return    int
   * @author Irv Bunton
   */
	private int procPrologue() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("procPrologue m_currentElementData.length(),m_currentElementData=" + m_currentElementData.length() + "," + m_currentElementData);}
		//#endif
		m_getPrologue = false;
		m_encodingStreamReader.setGetPrologue(false);
		String cencoding = getAttributeValue("encoding");
		if (cencoding == null) {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("parseStream Prologue cencoding,m_defEncoding=" + cencoding + "," + m_defEncoding);}
			//#endif
			cencoding = m_defEncoding;
		}
		m_encodingUtil.getEncoding(m_fileEncoding,
				cencoding);
		// Get doc encoding.  The encoding to translate
		// the bytes into.
		m_docEncoding = m_encodingUtil.getDocEncoding();
		return PROLOGUE;
	}

    protected int readBuf() throws IOException {
		/* Future
		m_offread = offread;
		m_lenRead = lenRead;
		*/
		if (m_getPrologue) {
			if (m_eof) {
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("readBuf() end of stream m_eof s true logging for prologue m_lenRead,m_readLen=" + m_lenRead + "," + m_readLen);}
				//#endif
				return -1;
			}
			int inputCharacter;
			if ((inputCharacter = m_encodingStreamReader.read()) == -1) {
				m_eof = true;
			}
			//#ifdef DLOGGING
//@			if (m_logReadChar && traceLoggable) {logger.trace("readBuf() get prologue returning inputCharacter=" + EncodingStreamReader.logInpChar(inputCharacter));}
			//#endif
			return inputCharacter;
		} else {
			if (m_offread < m_lenRead) {
				//#ifdef DLOGGING
//@				if (m_logReadChar && traceLoggable) {logger.trace("readBuf() returning m_cbuf[m_offread]=" + EncodingStreamReader.logcarr(m_cbuf, m_offread, m_lenRead));}
				//#endif
				return m_cbuf[m_offread++];
			} else if (m_eof) {
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("readBuf() end of stream m_eof s true m_lenRead,m_readLen=" + m_lenRead + "," + m_readLen);}
				//#endif
				return -1;
			} else {
				m_offread = 0;
				if ((m_lenRead = m_encodingStreamReader.read(m_cbuf, 0,
								m_readLen)) == -1) {
					m_offread = -1;
					m_eof = true;
					//#ifdef DLOGGING
//@					if (traceLoggable) {logger.trace("readBuf() end of stream m_lenRead,m_readLen=" + m_lenRead + "," + m_readLen);}
					//#endif
					return -1;
				//#ifdef DLOGGING
//@				} else {
//@					if (m_logReadChar && traceLoggable) {logger.trace("readBuf() m_lenRead,m_cbuf[m_offread],m_cbuf[m_offread + 1]=" + m_lenRead + "," + EncodingStreamReader.logcarr(m_cbuf, m_offread, m_lenRead) + "," + EncodingStreamReader.logcarr(m_cbuf, m_offread + 1, m_lenRead));}
				//#endif
				}
				return m_cbuf[m_offread++];
			}
		}
	}

    /** Parse next element */
    protected int parseStream(EncodingStreamReader is) throws IOException {
		
		boolean parsingElementName = false;
		boolean elementFound = false;
		boolean elementStart = false;
		boolean parsingElementData = false;
				
        char c;
        int inputCharacter = ((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
				readBuf());
		//#ifdef DLOGGING
//@		if (m_logReadChar && traceLoggable) {logger.trace("parseStream 1 m_lenRead,m_readLen,m_cbuf[m_offread]=" + m_lenRead + "," + m_readLen + "," + EncodingStreamReader.logcarr(m_cbuf, m_offread, m_lenRead));}
		//#endif
		int offread = m_offread;
		int lenRead = m_lenRead;
		try {
			if (inputCharacter != -1) {
				do {
					//#ifdef DLOGGING
//@					if (m_logChar && traceLoggable) {logger.trace("parseStream 2 offread,lenRead,m_offread,m_lenRead,m_currentElementName.length(),m_currentElementData.length()=" + offread + "," + lenRead  + "," + m_offread + "," + m_lenRead + "," + m_currentElementName.length() + "," + m_currentElementData.length());}
					//#endif
					if (offread == lenRead) {
						offread = m_offread;
						lenRead = m_lenRead;
					}
					c = (char)inputCharacter;
					//#ifdef DLOGGING
//@					boolean loggedChar = false;
					//#endif
					
					if (elementStart) {
						//#ifdef DLOGGING
//@						if (m_logChar && traceLoggable) {loggedChar = true; logger.trace("parseStream elementStart,c=" + elementStart + "," + c);}
						//#endif
						int parseResult = UNKNOWN_ELEMENT;
						switch (c) {
							case '/':
								elementStart = false;
								parsingElementName = false;
								parsingElementData = false;
								parseResult = parseBeginEntity(is, false, c,
													m_currentElementData
													//#ifdef DLOGGING
//@													,
//@													logger,
//@													traceLoggable,
//@													m_logChar
													//#endif
										);
								offread = m_offread;
								lenRead = m_lenRead;
								if (parseResult == CLOSE_TAG) {
									//#ifdef DLOGGING
//@									if (m_logChar && traceLoggable) {logger.trace("parseStream / m_currentElementData,c=" + m_currentElementData + "," + c);}
									//#endif
									continue;
								}
								break;
							// If we get ? or ! after '<' this is not an
							// element, it's a comment or prologe.
							case '?':
							case '!':
								if ((m_currentElementData.length() == 1) &&
										(m_currentElementData.charAt(0)=='<')) {
									elementStart = false;
									parsingElementName = false;
									parsingElementData = false;
									parseResult = parseBeginEntity(is, false, c,
											m_currentElementData
											//#ifdef DLOGGING
//@											,
//@											logger,
//@											traceLoggable,
//@											m_logChar
											//#endif
											);
									offread = m_offread;
									lenRead = m_lenRead;
									switch (parseResult) {
										case CDATA:
										case COMMENT:
										case CLOSE_TAG:
										case UNKNOWN_ELEMENT:
											continue;
										case END_DOCUMENT:
											break;
										case DOCTYPE:
										case STYLESHEET:
											return parseResult;
										case PROLOGUE:
											if (m_getPrologue) {
												return procPrologue();
											}
											break;
										default:
											break;
									}
								}
								break;
							default:
								break;
						}
						if (parseResult == END_DOCUMENT) {
							break;
						}
					}
					if(parsingElementName) {
						//#ifdef DLOGGING
//@						if (m_logChar && traceLoggable) {loggedChar = true; logger.trace("parseStream parsingElementName,c=" + parsingElementName + "," + c);}
						//#endif
						// Determine if we have found the end of the element
						// name and thus started element data.
						switch (c) {
							case ':':
								// For specified namespace, put it into element name
								if ((m_namespaces != null) &&
									(((m_namespaces.length >= 1) &&
									 m_namespaces[0].equals(
										m_currentElementName.toString())) ||
									((m_namespaces.length >= 2) &&
									m_namespaces[1].equals(
										m_currentElementName.toString())) ||
									((m_namespaces.length >= 3) &&
									m_namespaces[2].equals(
										m_currentElementName.toString())))) {
									m_currentElementName.append(c);
									break;
								}
								// Don't break after ':' (above) if not a part of
								// namespace as it is the end of the element
								// name.
							case ' ':
							case '/':
							case '\n':
							case '\r':
								parsingElementName = false;
								parsingElementData = true;
								elementStart = false;
								break;
							// Finding '>' is the end of an element name,
							// but we process it below.
							case '>':
								break;
							default:
								m_currentElementName.append(c);
								break;
						}
					}
					//#ifdef DLOGGING
//@					if (m_logChar && traceLoggable && !loggedChar) {loggedChar = true; logger.trace("parseStream miscelanious parsingElementName,elementFound,elementStart,parsingElementData,c=" + parsingElementName + "," + elementFound + "," + elementStart + "," + parsingElementData + "," + c);}
					//#endif
					// We found the beginning of a tag, so we start an element
					// name.
					if(c=='<') {
						elementStart = true;
						parsingElementName = true;
						parsingElementData = true;
						m_currentElementName.setLength(0);
						m_currentElementData.setLength(0);
					}            
					// If parsing element data, add to it.
					if(parsingElementData) {
						m_currentElementData.append(c);
					}
					// If we find end tag '>' can also be the
					// end of the prologe so we check.
					if ((c=='>') && (m_currentElementName.length()>0)) {
						elementFound = true;
						parsingElementName = false;
						elementStart = false;
						//#ifdef DLOGGING
//@						if (m_logChar) {
//@							if (traceLoggable) {logger.trace("parseStream m_currentElementName,m_currentElementData=" + m_currentElementName + "," + m_currentElementData);}
//@							if (!m_logRepeatChar) {
//@								m_logChar = false;
//@								m_logReadChar = false;
//@								m_encodingStreamReader.setLogChar(false);
//@								if (traceLoggable) {logger.trace("parseStream turning off m_logChar and m_logReadChar");}
//@							}
//@						}
						//#endif
						// If we find a tag not html without a prologue, need
						// to treat as default UTF-8 encoding for XML.
						// The caller using HTML type, must set the encoding.
						if (m_getPrologue && !htmlFile) {
							//#ifdef DLOGGING
//@							if (finerLoggable) {logger.finer("parseStream process end tag (>) if not HTML htmlFile,m_currentElementData=" + htmlFile + "," + m_currentElementData);}
							//#endif
							m_getPrologue = false;
							// This also sets our reader m_getPrologue to false.
							m_encodingUtil.getEncoding(m_fileEncoding,
									m_defEncoding);
							m_docEncoding = m_encodingUtil.getDocEncoding();
						}
					}

					//#ifdef DTEST
					//#ifdef DLOGGING
//@					if (m_debugTrace) {
//@						logger.finest("parseStream c=" + c);
//@						logger.finest("parseStream m_currentElementName=" + m_currentElementName);
//@						logger.finest("parseStream m_currentElementData=" + m_currentElementData);
//@						logger.finest("parseStream m_currentElementContainsText=" + m_currentElementContainsText);
//@						logger.finest("parseStream parsingElementName=" + parsingElementName);
//@						logger.finest("parseStream parsingElementData=" + parsingElementData);
//@						logger.finest("parseStream parsingElementData=" + parsingElementData);
//@						logger.finest("parseStream parsingElementData=" + parsingElementData);
//@						logger.finest("parseStream elementFound=" + elementFound);
//@						logger.finest("parseStream elementStart=" + elementStart);
//@					}
					//#endif
					//#endif
				}
					// If we have not found an element, keep parsing.
					// Otherwise, we get out of the loop.
				while (!elementFound && ((inputCharacter =
						((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
							readBuf())) != -1));
			}
			
			// Determine if we actually have element data or a tag
			// that ends without data/text (e.g. <br/> has no text)
			final int elen = m_currentElementData.length();
			if( ( elen >= 2) && (m_currentElementData.charAt( elen-2 )=='/') &&
				(m_currentElementData.charAt( elen-1 )=='>') ) {
				m_currentElementContainsText = false;
			} else {
				m_currentElementContainsText = true;
			}
			//#ifdef DLOGGING
//@			if (finerLoggable) {logger.finer("parseStream after loop inputCharacter,m_currentElementContainsText,m_currentElementName,m_currentElementData=" + EncodingStreamReader.logInpChar(inputCharacter) + "," + m_currentElementContainsText + "," + m_currentElementName + "," + m_currentElementData);}
			//#endif
			
		} catch (IOException e) {
//#ifdef DLOGGING
//@			logger.severe("parse read error ");
//#endif
			System.out.println("parse read error " + e + " " + e.getMessage());
			e.printStackTrace();
			throw e;
			/*
		} finally {
			if (inputCharacter != -1) {
				m_offread = offread;
				m_lenRead = lenRead;
			}
			*/
		}
		if( inputCharacter == -1 ) {
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("parseStream last stmt END_DOCUMENT m_currentElementContainsText,m_currentElementName,m_currentElementData=" + m_currentElementContainsText + "," + m_currentElementName.toString() + "," + m_currentElementData.toString());}
			//#endif
			return END_DOCUMENT;
		} else {
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("parseStream last stmt ELEMENT m_currentElementContainsText,m_currentElementName,m_currentElementData=" + m_currentElementContainsText + "," + m_currentElementName.toString() + "," + m_currentElementData.toString());}
			//#endif
			return ELEMENT;
		}
    }
    
	public int parseBlock(EncodingStreamReader is, StringBuffer sb,
			int eblock, boolean startsBlock, boolean endsSep,
			boolean reqEnd, String begin_block, String end_block)
	throws IOException {
		boolean beginFound = false;
		boolean beginChecked = false;
		char c;
		//#ifdef DLOGGING
//@		int irtn = UNKNOWN_ELEMENT;
		//#endif
        int inputCharacter = ((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
				readBuf());
		/* Future
		int offread = m_offread;
		int lenRead = m_lenRead;
		try {
		*/
			if (inputCharacter != -1) {
				do {
					/* Future
					//#ifdef DLOGGING
//@					if (m_logChar && traceLoggable) {logger.trace("parseBlock 1 offread,lenRead,m_offread,m_lenRead=" + offread + "," + lenRead  + "," + m_offread + "," + m_lenRead);}
					//#endif
					if (offread == lenRead) {
						offread = m_offread;
						lenRead = m_lenRead;
					}
					*/
					/* Future
					//#ifdef DLOGGING
//@					//if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE1 m_offread,offread", m_offread, offread);}
//@					//undo if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE1 m_lenRead,lenRead", m_lenRead, lenRead);}
					//#endif
					*/
					c = (char)inputCharacter;
					sb.append(c);
					if (c != '>') {
						/* Future
						//#ifdef DLOGGING
//@						if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE2 m_offread,offread", m_offread, offread);}
//@						if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE2 m_lenRead,lenread", m_lenRead, lenRead);}
//@						if (m_logReadChar && traceLoggable && (m_offread < m_lenRead)) {logger.assertLog("parseBlock NEC2 m_cbuf[m_offread],m_cbuf[offread]", m_cbuf[m_offread], m_cbuf[offread]);}
						//#endif
						*/
						continue;
					} else {
						if (!beginChecked) {
							beginChecked = true;
							int pos;
							if (startsBlock) {
								beginFound = sb.toString().startsWith(begin_block);
								pos = 0;
							} else {
								pos = sb.toString().indexOf(begin_block);
								beginFound = (pos >= 0);
							}
							if (beginFound && endsSep) {
								int epos;
								if ((epos = (begin_block.length() + pos)) <
										sb.length()) {
									switch (sb.charAt(epos)) {
										case '\n':
										case '\r':
										case ' ':
											break;
										default:
											beginFound = false;
									}
								}
							}
						}
						if (sb.toString().endsWith(end_block)) {
							if (beginFound) {
								//#ifdef DLOGGING
//@								irtn =
									//#else
									return
									//#endif
									eblock;
								//#ifdef DLOGGING
//@								return irtn;
								//#endif
							} else {
								//#ifdef DLOGGING
//@								irtn =
									//#else
									return
									//#endif
									UNKNOWN_ELEMENT;
								//#ifdef DLOGGING
//@								return irtn;
								//#endif
							}
						}
						if (!beginFound || !reqEnd) {
							break;
						}
					}
					/* Future
					//#ifdef DLOGGING
//@					if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE2 m_offread,offread", m_offread, offread);}
//@					if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE2 m_lenRead,lenread", m_lenRead, lenRead);}
//@					if (m_logReadChar && traceLoggable && (m_offread < m_readLen)) {logger.assertLog("parseBlock NEC2 m_cbuf[m_offread],m_cbuf[offread]", m_cbuf[m_offread], m_cbuf[offread]);}
					//#endif
					*/
				}
				while ((inputCharacter =
							((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
							 readBuf())) != -1);
			}
			if (inputCharacter == -1) {
				//#ifdef DLOGGING
//@				irtn =
					//#else
					return
					//#endif
					END_DOCUMENT;
				//#ifdef DLOGGING
//@				return irtn;
				//#endif
			}
			//#ifdef DLOGGING
//@			irtn =
				//#else
				return
				//#endif
				UNKNOWN_ELEMENT;
			//#ifdef DLOGGING
//@			return irtn;
			//#endif
			/* Future
		} finally {
			//#ifdef DLOGGING
//@			if (m_logReadChar && traceLoggable) {logger.trace("parseBlock irtn=" + irtn);}
//@			if (m_logChar && traceLoggable) {logger.trace("parseBlock 3 offread,lenRead,m_offread,m_lenRead=" + offread + "," + lenRead  + "," + m_offread + "," + m_lenRead);}
			//#endif
			if (offread != lenRead) {
				//#ifdef DLOGGING
//@				if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE3 m_offread,m_offread", m_offread, offread);}
//@				if (traceLoggable && m_logChar) {logger.assertLog("parseBlock NE3 m_lenRead,m_offread", m_lenRead, lenRead);}
				//#endif
			   m_offread = offread;
			}
		}
		*/
	}

	public Character skipBlanks(EncodingStreamReader is)
		throws IOException {
			char c;
			int inputCharacter;
			while ((inputCharacter =
						((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
						 readBuf())) != -1) {
				if ((c = (char)inputCharacter) != ' ') {
					return new Character(c);
				}
			}
			//#ifdef DLOGGING
//@			if (m_logReadChar && traceLoggable) {logger.trace("skipBlanks end of file inputCharacter=" + EncodingStreamReader.logInpChar(inputCharacter));}
			//#endif
			return null;
		}

	public Character getChar(EncodingStreamReader is)
	throws IOException {
		int inputCharacter;
		if ((inputCharacter =
				((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
					readBuf())) == -1) {
			//#ifdef DLOGGING
//@			if (m_logReadChar && traceLoggable) {logger.trace("getChar end of file inputCharacter=" + EncodingStreamReader.logInpChar(inputCharacter));}
			//#endif
			return null;
		} else {
			return new Character((char)inputCharacter);
		}
	}

	public int parseBeginEntity(EncodingStreamReader is, boolean readNext,
			char c,
			StringBuffer sb
			//#ifdef DLOGGING
//@			,
//@			Logger logger,
//@			boolean traceLoggable,
//@			boolean logChar
			//#endif
			)
	throws IOException {
		sb.append(c);
		if (readNext) {
			Character oc = skipBlanks(is);
			if (oc == null) {
				//#ifdef DLOGGING
//@				if (logChar && traceLoggable) {logger.trace("parseBeginEntity return end document c,sb=" + c + "," + sb.toString());}
				//#endif
				return END_DOCUMENT;
			}
			c = oc.charValue();
			//#ifdef DLOGGING
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity 1 c1,c2,sb=" + sb.charAt(sb.length() - 1) + "," +  c + "," + sb.toString());}
			//#endif
			sb.append(c);
			//#ifdef DLOGGING
//@		} else {
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity 1 c,sb=" + c + "," + sb.toString());}
			//#endif
		}
		if (c == '!') {
			Character oc = getChar(is);
			if (oc == null) {
				//#ifdef DLOGGING
//@				if (logChar && traceLoggable) {logger.trace("parseBeginEntity ! return end document c,sb=" + c + "," + sb.toString());}
				//#endif
				return END_DOCUMENT;
			}
			c = oc.charValue();
			//#ifdef DLOGGING
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity ! block 2 c,sb=" + c + "," + sb.toString());}
			//#endif
			sb.append(c);
			if (c == '[') {
				if (parseBlock(is, sb, CDATA, true, false, true,
							BEGIN_CDATA, END_CDATA) == CDATA) {
					//#ifdef DLOGGING
//@					if (logChar && traceLoggable) {logger.trace("parseBeginEntity ![ return CDATA document c,sb=" + c + "," + sb.toString());}
					//#endif
					return CDATA;
				}
				return UNKNOWN_ELEMENT;
			// Handle data type.
			} else if (c == 'D') {
				if (parseBlock(is, sb, DOCTYPE, true, true, false,
							BEGIN_DOCTYPE_REF, END_DOCTYPE_REF) == DOCTYPE) {
					//#ifdef DLOGGING
//@					if (logChar && traceLoggable) {logger.trace("parseBeginEntity possibily !DOCTYPE return DOCTYPE document c,sb=" + c + "," + sb.toString());}
					//#endif
					return DOCTYPE;
				}
				return UNKNOWN_ELEMENT;
			} else if (c == '-') {
				sb.setLength(sb.length() - 3);
				int parseResult;
				StringBuffer sbc = new StringBuffer("<!-");
				if ((parseResult = parseBlock(is, sbc,
							COMMENT, true, false, true,
							BEGIN_COMMENT, END_COMMENT)) ==
						COMMENT) {
					//#ifdef DLOGGING
//@					if (logChar && traceLoggable) {logger.trace("parseBeginEntity - return COMMENT c,sb=" + c + "," + sb.toString());}
					//#endif
					return COMMENT;
				} else if (parseResult == END_DOCUMENT) {
					//#ifdef DLOGGING
//@					if (logChar && traceLoggable) {logger.trace("parseBeginEntity - return end document c,sb=" + c + "," + sb.toString());}
					//#endif
					return END_DOCUMENT;
				}
				return parseResult;
			} else {
				return parseBlock(is, sb, UNKNOWN_ELEMENT, true, false, false,
							"<", ">");
			}
		} else if (c == '/') {
			int parseResult = parseBlock(is, sb, CLOSE_TAG, false, false, false,
										BEGIN_CLOSE_TAG, END_CLOSE_TAG);
			//#ifdef DLOGGING
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity c == / return  parseResult,c,sb=" + parseResult + "," + c + "," + sb.toString());}
			//#endif
			switch (parseResult) {
				case CLOSE_TAG:
					int clenm1 = sb.length() - 2;
					while ((clenm1 > 0) && (sb.charAt(clenm1) == ' ')) {
						sb.deleteCharAt(clenm1--);
					}
					if (clenm1 <= 1) {
						return UNKNOWN_ELEMENT;
					}
					break;
				case UNKNOWN_ELEMENT:
					parseResult = PARTIAL_TEXT;
				default:
					break;
			}
			//#ifdef DLOGGING
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity c != !,/ return  parseResult,c,sb=" + parseResult + "," + c + "," + sb.toString());}
			//#endif
			return parseResult;
		} else if (c == '?') {
			int parseResult;
			if ((parseResult = parseBlock(is, sb, PROLOGUE, true, true, false,
							BEGIN_PROLOGUE, END_PROLOGUE)) == PROLOGUE) {
				return parseResult;
			}
			//#ifdef DLOGGING
//@			if (logChar && traceLoggable) {logger.trace("parseBeginEntity ? document c,parseResult,sb=" + c + "," + parseResult + "," + sb.toString());}
			//#endif
			if ((parseResult == UNKNOWN_ELEMENT) &&
					sb.toString().startsWith(BEGIN_STYLESHEET) &&
					sb.toString().endsWith(END_STYLESHEET)) {
				return STYLESHEET;
			}
			return parseResult;
		} else {
			return parseBlock(is, sb, UNKNOWN_ELEMENT, true, false, false,
						"<", ">");
		}
	}

    /** Parse next element */
	public int parse() throws IOException {
		return parseStream(m_encodingStreamReader);
	}
		
    /** Get element name */
    public String getName() {
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("m_currentElementName=" + m_currentElementName);}
		//#endif
        return m_currentElementName.toString();
    }
    
    /** Get element text including inner xml
	  * If no text, return empty string "" */
    private String getTextStream(EncodingStreamReader is, final boolean convEnts) throws IOException {
        
		if(!m_currentElementContainsText) {
			return "";
		}
		
		String text = "";
		int inputCharacter;
		try {
			char c;
			char lastChars[] = {' ', ' ', ' '};
			
			char elementNameChars[] = new char[3];
			int elen = m_currentElementName.length();
			switch (elen) {
				case 0:
					return "";
				case 1:
		  			elementNameChars[0] = '<';
		  			elementNameChars[1] = '/';
		  			elementNameChars[2] = m_currentElementName.charAt( 0 );
					break;
				case 2:
		  			elementNameChars[0] = '/';
		  			elementNameChars[1] = m_currentElementName.charAt( 0 );
		  			elementNameChars[2] = m_currentElementName.charAt( 1 );
					break;
				default:
					// Copy the last 3 characters indexes begin at elen -3
					// to before elen to the char array.
					m_currentElementName.toString().getChars(elen - 3, elen,
							elementNameChars, 0);
					break;
			}
			//#ifdef DLOGGING
//@			if (m_logChar && traceLoggable) {logger.trace("getTextStream elementNameChars=" + new String(elementNameChars));}
			//#endif
			final String endCurrentElement = new StringBuffer("</").append(
						m_currentElementName.toString()).toString();
			//#ifdef DLOGGING
//@			if (m_logChar && traceLoggable) {logger.trace("getTextStream endCurrentElement=" + endCurrentElement);}
			//#endif
			StringBuffer textBuffer = new StringBuffer(
					endCurrentElement.length());
			if ((inputCharacter =
					((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
						readBuf())) == -1) {
				return "";
			}
			/* Future
			int offread = m_offread;
			int lenRead = m_lenRead;
			try {
			*/
				do {
					/* Future
					if (offread == lenRead) {
						offread = m_offread;
						lenRead = m_lenRead;
					}
					//#ifdef DLOGGING
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE1 m_offread,m_offread", m_offread, offread);}
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE1 m_lenRead,m_offread", m_lenRead, lenRead);}
					//#endif
					*/
					c = (char)inputCharacter;
					//#ifdef DLOGGING
//@					if (m_logChar && traceLoggable) {logger.trace("getTextStream c,textBuffer=" + c + "," + textBuffer.toString());}
					//#endif
					if (c == '<') {
						/*
						m_offread = offread;
						m_lenRead = lenRead;
						//#ifdef DLOGGING
//@						if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE m_offread,m_offread", m_offread, offread);}
//@						if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE m_lenRead,m_offread", m_lenRead, lenRead);}
						//#endif
						*/
						int parseResult = parseBeginEntity(is, true, c,
								textBuffer
								//#ifdef DLOGGING
//@								,
//@								logger,
//@								traceLoggable,
//@								m_logChar
								//#endif
								);
						/* Future
						offread = m_offread;
						lenRead = m_lenRead;
						*/
						if (parseResult == CLOSE_TAG) {
							final int tlen = textBuffer.length();
							c = textBuffer.charAt(tlen - 1);
							textBuffer.setLength(tlen - 1);
							textBuffer.getChars(tlen - 4, tlen - 1, lastChars, 0);
							//#ifdef DLOGGING
//@							if (m_logChar && traceLoggable) {logger.trace("getTextStream CLOSE_TAG c,lastChars=" + c + "," + new String(lastChars));}
//@							if (m_logChar && traceLoggable) {logger.trace("getTextStream CLOSE_TAG c,textBuffer=" + c + "," + textBuffer.toString());}
							//#endif
						} else if (parseResult == END_DOCUMENT) {
							break;
						} else {
							continue;
						}
					}
					//System.out.print(c);

					if( (c == '>') &&
							(lastChars[0] == elementNameChars[0]) &&
							(lastChars[1] == elementNameChars[1]) &&
							(lastChars[2] == elementNameChars[2]) &&
							textBuffer.toString().endsWith(endCurrentElement)) {
						break;
					}
					lastChars[0] = lastChars[1];
					lastChars[1] = lastChars[2];
					lastChars[2] = c;
					textBuffer.append(c);
					/* Future
					//#ifdef DLOGGING
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE2 m_offread,m_offread", m_offread, offread);}
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE2 m_lenRead,m_offread", m_lenRead, lenRead);}
//@					if (m_logReadChar && traceLoggable && (m_lenRead < m_offread)) {logger.assertLog("getTextStream NEC m_cbuf[m_offread],m_cbuf[offread++]", m_cbuf[m_offread], m_cbuf[offread]);}
					//#endif
					if (m_lenRead < m_offread) {
						offread++;
					}
					*/
				}
				while ((inputCharacter =
							((m_offread < m_lenRead) ? m_cbuf[m_offread++] :
							 readBuf())) != -1);
				/* Future
			} finally {
				if (inputCharacter != -1) {
					//#ifdef DLOGGING
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE3 m_offread,m_offread", m_offread, offread);}
//@					if (traceLoggable && m_logChar) {logger.assertLog("getTextStream NE3 m_lenRead,m_offread", m_lenRead, lenRead);}
					//#endif
					m_offread = offread;
					m_lenRead = lenRead;
				}
			}
			*/

			if (m_docEncoding.length() == 0) {
				text = textBuffer.toString();
			} else {
				try {
					// We read the bytes in as ISO8859_1, so we must get them
					// out as that and then encode as they should be.
					if (m_fileEncoding.length() == 0) {
						text = new String(textBuffer.toString().getBytes(),
										  m_docEncoding);
					} else {
						text = new String(textBuffer.toString().getBytes(
									m_fileEncoding), m_docEncoding);
					}
				} catch (IOException e) {
					//#ifdef DLOGGING
//@					logger.severe("getTextStream Could not convert string from,to" + m_fileEncoding + "," + m_docEncoding, e);
					//#endif
					System.out.println("getTextStream Could not convert string " +
							"from,to=" + m_fileEncoding + "," + m_docEncoding +
							" " + e + " " + e.getMessage());
					e.printStackTrace();
					text = textBuffer.toString();
				}
			}
			// Save memory.
			textBuffer = (StringBuffer)m_nullPtr;
			text = MiscUtil.replace(text, endCurrentElement, "");
			
			/** Handle some entities and encoded characters */
			text = MiscUtil.replace(text, BEGIN_CDATA, "");
			int flen = text.length();
			text = MiscUtil.replace(text, END_CDATA, "");
			if (((flen == text.length()) || convEnts) &&
					(text.indexOf('&') >= 0)) {
				text = m_encodingUtil.replaceAlphaEntities(text);
				// No need to convert from UTF-8 to Unicode using replace
				// umlauts now because it is done with new String...,encoding.

				// Replace numeric entities including &#8217;, &#8216;
				// &#8220;, and &#8221;
				text = m_encodingUtil.replaceNumEntity(text);
			}

			// Replace special chars like left quote, etc.
			text = m_encodingUtil.replaceSpChars(text);
			
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("getTextStream Could not read a char run time.", t);
//#endif
			System.out.println("getTextStream Could not read a char run time." + t +
					           " " + t.getMessage());
			t.printStackTrace();
		}
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("getTextStream text=" + text);}
		//#endif
		return text;
    }

	//#ifdef DTEST
//@    public String getText() throws IOException {
//@		return getText(true);
//@	}
	//#endif

    /** Get element text including inner xml */
    public String getText(boolean convEnts) throws IOException {
		return getTextStream(m_encodingStreamReader, convEnts);
	}

    /** 
     * Get attribute value from current element 
     */
    public String getAttributeValue(String attributeName) {
        
		try {
			/** Check whatever the element contains given attribute */
			String ccurrentElementData = m_currentElementData.toString();
			int attributeStartIndex = ccurrentElementData.indexOf(attributeName);
			if( attributeStartIndex<0 ) {
				//#ifdef DLOGGING
//@				if (finerLoggable) {logger.finer("getAttributeValue attribute attributeStartIndex=" + attributeStartIndex);}
				//#endif
				return null;
			}
			
			/** Calculate actual value start index */
			int valueStartIndex = attributeStartIndex + attributeName.length() + 2;
			
			/** Check the attribute value end index */
			int valueEndIndex = ccurrentElementData.indexOf('\"', valueStartIndex);
			if( valueEndIndex<0 ) {
				/** Check using windows quote account for other unexplained
				    quotes */
				if ((valueStartIndex + 1) < ccurrentElementData.length()) {
					String beginQuote = ccurrentElementData.substring(
							valueStartIndex - 1, valueStartIndex);
					valueEndIndex = ccurrentElementData.indexOf(beginQuote,
							valueStartIndex);
				}
				if( valueEndIndex<0 ) {
					return null;
				}
			}
			
			/** Parse value */
			String value = ccurrentElementData.substring(valueStartIndex, valueEndIndex);
			if (m_docEncoding.length() != 0) {
				// We read the bytes in as ISO8859_1, so we must get them
				// out as that and then encode as they should be.
				if (m_fileEncoding.length() == 0) {
					value = new String(value.getBytes(),
									  m_docEncoding);
				} else {
					value = new String(value.getBytes(
								m_fileEncoding), m_docEncoding);
				}
			}
			//#ifdef DLOGGING
//@			if (finerLoggable) {logger.finer("getAttributeValue attribute value=" + value);}
			//#endif
					
			return value;
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("getAttributeValue error.", t);
//#endif
			System.out.println("getAttributeValue error." + t + " " +
					           t.getMessage());
			return null;
		}
    }

	static public String convAttrData(String attrData) {
		return MiscUtil.replace(MiscUtil.replace(
				MiscUtil.replace(attrData, "&", "&amp;"), "<", "&#60;"),
				">", "&#62;");
	}
    
	static public String convAttrUrlData(String attrData) {
		return MiscUtil.replace(MiscUtil.replace(
				MiscUtil.replace(attrData, "&", "&amp;"), "<", "%3C"),
				">", "%3E");
	}
    
  /**
   * Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
   *
   * @return    int
   * @author Irv Bunton
   */
	public int parseXmlElement() throws IOException {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("parseXmlElement begin");}
		//#endif
        int parsingResult;
		if (((parsingResult = parse()) == ELEMENT) &&
			getName().equals("link")) {
			while (((parsingResult = parse()) == ELEMENT) &&
				getName().equals("meta")) {
			}
		}
		while ((parsingResult == XmlParser.PROLOGUE) ||
				(parsingResult == XmlParser.DOCTYPE) ||
				(parsingResult == XmlParser.STYLESHEET)) {
			if (parsingResult == XmlParser.PROLOGUE) {
				procPrologue();
			}
			parsingResult = parse();
		}
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("parseXmlElement end parsingResult=" + parsingResult);}
		//#endif
		return parsingResult;
	}

    /** 
     * Get namesapces.  Return two dimension array with the first column
	 * the namespace and the second on the URL for the namespace.
     */
    public String [][] parseNamespaces() {
        
		try {
			/** Check whatever the element contains given attribute */
			String ccurrentElementData = m_currentElementData.toString();
			Vector vnamespaces = new Vector();
			Vector vnamesurls = new Vector();
			int nspos = 0;
			while ((nspos = ccurrentElementData.indexOf("xmlns:", nspos)) >= 0) {
				nspos+= 6;
				int eqpos = ccurrentElementData.indexOf('=', nspos);
				if (eqpos < 0) {
					continue;
				}
				String xmlns = ccurrentElementData.substring(nspos, eqpos);
				int qpos = ccurrentElementData.indexOf('\"', eqpos + 2);
				if (qpos < 0) {
					continue;
				}
				String url = ccurrentElementData.substring(eqpos + 2, qpos);
				//#ifdef DLOGGING
//@				if (finerLoggable) {logger.finer("parseNamespaces xmlns,url=" + xmlns + "," + url);}
				//#endif
				vnamespaces.addElement(xmlns);
				vnamesurls.addElement(url);
			}
			if (vnamespaces.size() == 0) {
				return new String[0][0];
			}
			int vlen = vnamespaces.size();
			String [][] ns = new String[2][vlen];
			for (int ic = 0; ic < vlen; ic++) {
				ns[0][ic] = (String)vnamespaces.elementAt(ic);
				ns[1][ic] = (String)vnamesurls.elementAt(ic);
			}
			return ns;
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("parseNamespaces error.", t);
//#endif
			System.out.println("parseNamespaces error." + t + " " +
					           t.getMessage());
			return new String[0][0];
		}
    }
    
    public void setNamespaces(String [] namespaces) {
        this.m_namespaces = namespaces;
    }

    public String [] getNamespaces() {
        return (m_namespaces);
    }

    public void setDocEncoding(String docEncoding) {
        this.m_docEncoding = docEncoding;
    }

    public String getDocEncoding() {
        return (m_docEncoding);
    }

    public boolean isWindows() {
        return (m_encodingUtil.isWindows());
    }

    public boolean isUtf() {
        return (m_encodingUtil.isUtf());
    }

	//#ifdef DTEST
//@    public EncodingUtilIntr getEncodingUtil()
	//#else
    public EncodingUtil getEncodingUtil()
	//#endif
	{
        return (m_encodingUtil);
    }

    public void setHtmlFile(boolean htmlFile) {
        this.htmlFile = htmlFile;
    }

    public boolean isHtmlFile() {
        return (htmlFile);
    }

	//#ifdef DLOGGING
	//#ifdef DTEST
//@    public void setLogChar(boolean logChar) {
//@        this.m_logChar = logChar;
//@    }
//@
//@    public boolean isLogChar() {
//@        return (m_logChar);
//@    }
//@    public void setLogReadChar(boolean logReadChar) {
//@        this.m_logReadChar = logReadChar;
		//#ifdef DLOGGING
//@		if (logReadChar) {
//@			m_encodingStreamReader.setLogChar(true);
//@		}
		//#endif
//@    }
//@
//@    public boolean isLogReadChar() {
//@        return (m_logReadChar);
//@    }
//@
//@    public void setLogRepeatChar(boolean logRepeatChar) {
//@        this.m_logRepeatChar = logRepeatChar;
//@    }
//@
//@    public boolean isLogRepeatChar() {
//@        return (m_logRepeatChar);
//@    }
//@
	//#endif
	//#endif

}
//#endif
