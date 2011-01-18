//--Need to modify--#preprocess
/*
 * EncodingUtil.java
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
 * IB 2010-03-14 1.11.5RC2 Combine classes to save space.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-15 1.11.5Dev14 Use getSysPropStarts to get system properties and see if it starts with a string.  This can help with initialization of static variables.
 * IB 2010-11-16 1.11.5Dev14 Add default value of null for getSysProperty, getSysPermission, and getSysPropStarts.
 * IB 2011-01-01 1.11.5Dev15 In EncodingUtil, initialize formally static vars created by internal functions in getInstance to help reduce problems with not being able to save data.  Put all such vars in one block.  Make all methods non-static except getInstance and getEncodingUtil.  Move m_sglStatExcs to be first as it is depended upon by many vars.  Move vars with no or only m_sglStatExcs to follow init of m_sglStatExcs.  Move vars set by methods depending on other initialized vars to be second:  Only m_sglConvCp1252 was like this.  Have m_sglStatExcs be set to null after all initializations if it has no entries.  Remove code creating m_sglStatExcs from methods except for getStatExcs.  Have all methods ued for initialization have try/catch Throwable blocks to reduce initialization problems.
 * IB 2011-01-01 1.11.5Dev15 Do modification of the bytes if 16 or 32 bits.
 * IB 2011-01-01 1.11.5Dev15 Better logging.
 * IB 2011-01-01 1.11.5Dev15 Have replace... methods not be static.
 * IB 2011-01-01 1.11.5Dev15 Have set/getBitNbrDoc to keep track of how many bits make up a character.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use getEncodingUtil and getEncodingStreamReader to create EncodingUtil and EncodingStreamReader respectively to eliminate cross referencing in constructors.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define CLDC define
@DCLDCVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DFULLVERS
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
//#ifdef DTEST
import com.substanceofcode.utils.EncodingUtilIntr;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
final public class EncodingUtil
//#ifdef DTEST
implements EncodingUtilIntr
//#endif
{
    
	final public boolean m_sglMidpIso;
	final public String m_sglIsoEncoding;
	final public boolean m_sglMidpWin;
	final public String m_sglwinEncoding;
	final public boolean m_sglMidpUni;
	final String[] m_sglIsoCommonEntities;
	final String[] m_sglIsoSpecialEntities;
	final char[] m_sglIsoSpecialValues;
	final char[] m_sglIsoCommValues;
	final String[] m_sglIsoLatin1Entities;
	// Convert windows characters in iso 8859 control range to ISO
	// (not the actual character, but a good fix or remove if no equivalent)
	final public char[] m_sglWinIsoConvx80;

	// Convert uni chars to equivalent windows characters in the 0x80 - 0x9f
	// range.
	/* FUTURE?
	public char[] m_uniWinConvx80 = initUniWinConvx80();
	*/
	// See if windows cp-1252 is supported.
	public boolean m_sglHasWinEncoding;
	// See if ISO8859-1 is supported.
	final public boolean m_sglHasIso8859Encoding;
	final public Hashtable m_sglConvIso88591;
	final public Hashtable m_sglConvCp1252;
	final public boolean m_sglConvWinUni;
	volatile Vector m_sglStatExcs; // Exceptions encountered
	//#ifdef DTEST
    final private boolean m_debugTrace = false;  // True if want to trace more
	//#endif
    public final String SGL_WRIGHT_SGLE_QUOTE;
    public final String SGL_RIGHT_SGLE_QUOTE;

    final public byte NOT_UTF = (byte)0;         // Not UTF.
    final public byte UTF_1 = (byte)1;         // UTF-1
    final public byte UTF_7 = (byte)7;         // UTF-7
    final public byte UTF_8 = (byte)8;         // UTF-8
    final public byte UTF_16 = (byte)16;         // UTF-16
    final public byte UTF_32 = (byte)32;         // UTF-32
    final public byte BIG_5 = (byte)65;         // BIG-5
	// Left single quote in cp-1252 (Windows) encoding.
    public static final char CWSGL_LOW9_QUOTE = 0x82; // #130;
    public static final char CWDBL_LOW9_QUOTE = 0x84; // #132;
    public static final char CWLEFT_SGL_QUOTE = 0x91; // #145;
    public static final char CWRIGHT_SGL_QUOTE = 0x92; // #146;
    private static final char [] CAWRIGHT_SGL_QUOTE = {CWRIGHT_SGL_QUOTE};
    public static final char CWLEFT_DBL_QUOTE = 0x93; // #147;
    public static final char CWRIGHT_DBL_QUOTE = 0x94; // #148;
    public static final char CWEN_DASH = 0x96; // #150;
    public static final char CWEM_DASH = 0x97; // #151;
	// Left single quote in Unicode (utf-16) encoding.
	// Long dash a.k.a en dash
    public static final char CEN_DASH = 0x2013;
    public static final char CEM_DASH = 0x2014;
    public static final char CLEFT_SGL_QUOTE = 0x2018;
    public static final char CRIGHT_SGL_QUOTE = 0x2019;
    private static final char [] CARIGHT_SGL_QUOTE = {CRIGHT_SGL_QUOTE};
    public static final char CSGL_LOW9_QUOTE = 0x201A;
    private static final char CLEFT_DBL_QUOTE = 0x201C;
    private static final char CRIGHT_DBL_QUOTE = 0x201D;
    public static final char CDBL_LOW9_QUOTE = 0x201E;
    public static final char CA_UMLAUTE = (char)228;
	/* FUTURE?
    private static final char CO_UMLAUTE = (char)246;
	*/
    public static final char CNON_BREAKING_SP = (char)160;
    
    private EncodingStreamReader m_encodingStreamReader;
    private String m_docEncoding = "";  // Default for XML is UTF-8.
	                                    // unexpected UTF-16.
    private boolean m_utf = false;  // Doc is utf.
    private boolean m_modBit16o32 = false;  // Doc is not 16 or 32 bit.
    private byte m_bitNbrDoc = NOT_UTF;
    private boolean m_windows = false;  // True if windows code space
	Vector m_excs = null; // Exceptions encountered
	volatile private static EncodingUtil m_singleton = null;

	//#ifdef DLOGGING
    final private Logger logger;
    final private boolean fineLoggable;
    final private boolean traceLoggable;
	//#endif
    
    /** Creates a new instance of EncodingUtil */
    private EncodingUtil(boolean singleton) {
		//#ifdef DLOGGING
		logger = Logger.getLogger("EncodingUtil");
		fineLoggable = logger.isLoggable(Level.FINE);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
		m_encodingStreamReader = null;
		if (singleton) {
			m_sglStatExcs = new Vector();
			m_sglMidpIso = (FeatureMgr.getSysPropStarts(
						"microedition.encoding", null, "Unable to get micro encoding.", null,
						"iso-8859") ||
					FeatureMgr.getSysPropStarts(
						"microedition.encoding", null, "Unable to get micro encoding.", null,
						"iso8859"));
			m_sglIsoEncoding = initIsoEncoding();
			m_sglMidpWin = (FeatureMgr.getSysPropStarts(
						"microedition.encoding", null, "Unable to get micro encoding.", null,
						"cp") ||
					FeatureMgr.getSysPropStarts(
						"microedition.encoding", null, "Unable to get micro encoding.", null,
						"windows"));
			m_sglwinEncoding = initWinEncoding();

			m_sglMidpUni = FeatureMgr.getSysPropStarts(
					"microedition.encoding", null, "Unable to get micro encoding.", null,
					"utf-8");
			m_sglIsoCommonEntities =
				new String[] {"iexcl", "cent", "pound", "curren", "yen",
					"brvbar", "sect", "uml", "copy", "ordf",
					"laquo", "not", "shy", "reg", "macr",
					"deg", "plusmn", "sup2", "sup3", "acute",
					"micro", "para", "middot", "cedil", "sup1",
					"ordm", "raquo", "frac14", "frac12", "frac34",
					"iquest"};

			m_sglIsoSpecialEntities =
				new String[] {"ndash", // en dash 
					"mdash", // em dash 
					"lsquo", // left single quotation mark 
					"rsquo", // right single quotation mark 
					"sbquo", // single low-9 quotation mark 
					"ldquo", // left double quotation mark 
					"rdquo", // right double quotation mark 
					"bdquo"}; // double low-9 quotation mark 

			m_sglIsoSpecialValues =
				new char[] {'-', // en dash 
					'-', // em dash 
					'\'', // left single quotation mark 
					'\'', // right single quotation mark 
					'\'', // single low-9 quotation mark 
					'\"', // left double quotation mark 
					'\"', // right double quotation mark 
					'\"'}; // double low-9 quotation mark 

			m_sglIsoCommValues = 
				new char[] {0xA1, 0xA2, 0xA3, 0xA4, 0xA5,
					0xA6, 0xA7, 0xA8, 0xA9, 0xAA,
					0xAB, 0xAC, 0xAD, 0xAE, 0xAF,
					0xB0, 0xB1, 0xB2, 0xB3, 0xB4,
					0xB5, 0xB6, 0xB7, 0xB8, 0xB9,
					0xBA, 0xBB, 0xBC, 0xBD, 0xBE,
					0xBF};

			m_sglIsoLatin1Entities = 
				new String[] {"Agrave", "Aacute", "Acirc", "Atilde", "Auml",
					"Aring", "AElig", "Ccedil", "Egrave", "Eacute", "Ecirc", "Euml",
					"Igrave", "Iacute", "Icirc", "Iuml", "ETH", "Ntilde", "Ograve",
					"Oacute", "Ocirc", "Otilde", "Ouml", "times", "Oslash", "Ugrave",
					"Uacute", "Ucirc", "Uuml", "Yacute", "THORN", "szlig", "agrave",
					"aacute", "acirc", "atilde", "auml", "aring", "aelig", "ccedil",
					"egrave", "eacute", "ecirc", "euml", "igrave", "iacute", "icirc",
					"iuml", "eth", "ntilde", "ograve", "oacute", "ocirc", "otilde",
					"ouml", "divide", "oslash", "ugrave", "uacute", "ucirc", "uuml",
					"yacute", "thorn", "yuml"};

			// Convert windows characters in iso 8859 control range to ISO
			// (not the actual character, but a good fix or remove if no equivalent)
			m_sglWinIsoConvx80 = initWinIsoConv();
			m_sglHasWinEncoding = initHasWinEncoding();
			m_sglHasIso8859Encoding = initHasIso8859Encoding();
			m_sglConvIso88591 = initAlphaIso88591();
			m_sglConvWinUni = initConvWinUni();
    		SGL_WRIGHT_SGLE_QUOTE = new String(CAWRIGHT_SGL_QUOTE);
    		SGL_RIGHT_SGLE_QUOTE = new String(CARIGHT_SGL_QUOTE);
			m_sglConvCp1252 = initAlphaCp1252();

		} else {
			m_sglStatExcs = null;
			m_sglMidpIso = false;
			m_sglIsoEncoding = null;
			m_sglMidpWin = false;
			m_sglwinEncoding = null;
			m_sglMidpUni = false;
			m_sglIsoCommonEntities = null;
			m_sglIsoSpecialEntities = null;
			m_sglIsoSpecialValues = null;

			m_sglIsoCommValues = null;

			m_sglIsoLatin1Entities = null;
			m_sglWinIsoConvx80 = null;
			/* FUTURE?
			m_uniWinConvx80 = null;
			*/
			m_sglHasWinEncoding = false;
			m_sglHasIso8859Encoding = false;
			m_sglConvIso88591 = null;
			m_sglConvWinUni = false;
    		SGL_WRIGHT_SGLE_QUOTE = null;
    		SGL_RIGHT_SGLE_QUOTE = null;
			m_sglConvCp1252 = null;
		}
	}

    /** Creates a new instance of EncodingUtil */
    static public EncodingUtil getEncodingUtil(InputStream inputStream) {
		EncodingUtil encodingUtil = new EncodingUtil(false);
		encodingUtil.getQuickInstance();
		if (inputStream != null) {
			encodingUtil.setEncodingStreamReader(
				EncodingStreamReader.getEncodingStreamReader(inputStream,
					encodingUtil));
		}
		return encodingUtil;
    }

    /** Get instance */
	//#ifdef DCLDCV11
    public static EncodingUtil getInstance()
	//#else
    public static synchronized EncodingUtil getInstance()
	//#endif
	{
		if (m_singleton != null) {
			return m_singleton;
		} else {
			//#ifdef DCLDCV11
			synchronized(EncodingUtil.class) {
				//#endif
				//#ifdef DLOGGING
				Logger.getLogger("EncodingUtil").info(
						"Constructor midlet,m_singleton=" + FeatureMgr.getMidlet() + "," + m_singleton);
				//#endif
				if (m_singleton == null) {
					m_singleton = new EncodingUtil(true);
				}
				return m_singleton;
				//#ifdef DCLDCV11
			}
			//#endif
		}
    }
    
    /** Get instance */
    public EncodingUtil getQuickInstance() {
		if (m_singleton != null) {
			return m_singleton;
		} else {
			return getInstance();
		}
	}

    public void getEncoding(final String fileEncoding, final String encoding) {
		getEncoding(m_singleton.m_sglHasIso8859Encoding, m_singleton.m_sglIsoEncoding, m_singleton.m_sglHasWinEncoding,
				m_singleton.m_sglwinEncoding, fileEncoding, encoding);
	}

	/**  Determine the encoding based on what is passed in as well
	  as if/when strings are to be further encoded.  Also decide to
	  modify bytes read.  
	 **/

    public void getEncoding(final boolean hasIso8859Encoding,
			final String isoEncoding, final boolean hasWinEncoding,
			final String winEncoding, final String fileEncoding,
			final String encoding) {
		//#ifdef DLOGGING
        if (fineLoggable) {logger.fine("getEncoding encoding=" + encoding);}
		//#endif
		String cencoding = encoding;
		boolean modBit16o32 = m_encodingStreamReader.isModBit16o32();
		byte bitNbrDoc = m_encodingStreamReader.getBitNbrDoc();
		boolean futf = m_encodingStreamReader.isUtfDoc();
        // If there is a second char, don't stop splitting until we
        // return that char as input.
        if (cencoding == null) {
			if (modBit16o32) {
			   cencoding = futf ? getBitEncoding(bitNbrDoc) : null;
			}
			if (cencoding == null) {
			   cencoding = "UTF-8";
			}
        }
        cencoding = cencoding.toUpperCase();
		boolean modEncoding = m_encodingStreamReader.isModEncoding();
		m_utf = m_encodingStreamReader.isUtfDoc();
		m_windows = false;
		String docEncoding = fileEncoding;
		// Only need to convert from 2 byte to 1 byte and vsa versa.
        if (cencoding.equals("UTF-1") || cencoding.equals("UTF1")) {
            docEncoding = "UTF-1";
            modEncoding = false;
            modBit16o32 = false;
			bitNbrDoc = UTF_1;
			m_utf = true;
        } else if (cencoding.equals("UTF-16") || cencoding.equals("UTF16")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = true;
            modEncoding = true;
			bitNbrDoc = UTF_16;
			m_utf = true;
			// Don't do doc encoding as the stream reader does it.
            docEncoding = "";
        } else if (cencoding.equals("UTF-32") || cencoding.equals("UTF32")) {
			// If utf-32, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = true;
            modEncoding = true;
			bitNbrDoc = UTF_32;
			m_utf = true;
			// Don't do doc encoding as the stream reader does it.
            docEncoding = "";
			// Have this last for UTF to catch missed encodings which
			// are not 16/32 bit.
		} else if (cencoding.equals("UTF-8") || cencoding.equals("UTF8") ||
				(m_utf && !modBit16o32)) {
            docEncoding = "UTF-8";
            modEncoding = false;
            modBit16o32 = false;
			bitNbrDoc = UTF_8;
			m_utf = true;
        } else if (cencoding.equals("BIG5") || cencoding.equals("BIG-5")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = BIG_5;
            modEncoding = false;
			m_utf = false;
            docEncoding = "BIG5";

        } else if (cencoding.equals("BIG-HSCS") ||
			cencoding.equals("BIG_HSCS") || cencoding.equals("BIG-HSCS")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "BIG_HSCS";

        } else if (cencoding.equals("windows-31j") ||
			cencoding.equals("MS932")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "MS932";
        } else if (cencoding.equals("x-mswin-936") ||
			cencoding.equals("MS936")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "MS936";
        } else if (cencoding.equals("EUC_CN") || cencoding.equals("x-EUC-CN")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "EUC_CN";
        } else if (cencoding.equals("SJIS") || cencoding.equals("Shift-JIS")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "SJIS";
        } else if (cencoding.equals("EUC_JP") || cencoding.equals("EUC-JP")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "EUC_JP";
        } else if (cencoding.equals("EUC_JP_LINUX") || cencoding.equals("EUC-JP-LINUX")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "EUC_JP_LINUX";
        } else if (cencoding.equals("ISO-2022-JP") || cencoding.equals("ISO2022JP")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "ISO2022JP";
        } else if (cencoding.equals("x-windows-949") ||
				cencoding.equals("MS949")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "MS949";
        } else if (cencoding.equals("x-windows-950") ||
				cencoding.equals("MS950")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "MS950";
        } else if (cencoding.equals("x-MS950-HKSCS") ||
				cencoding.equals("MS950_HKSCS")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "MS950_HKSCS";
        } else if (cencoding.equals("ISCII91")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "ISCII91";
        } else if (cencoding.equals("EUC_TW") || cencoding.equals("EUC-TW")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "EUC_TW";
        } else if (cencoding.equals("TIS-620") || cencoding.equals("TIS620")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "TIS620";
        } else if (cencoding.equals("GBK")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
            modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_utf = false;
            docEncoding = "GBK";
		} else if (cencoding.startsWith("ISO-8859")) {
			if (hasIso8859Encoding) {
				if (isoEncoding.indexOf("-") == -1) {
					docEncoding = MiscUtil.replace(cencoding, "ISO-",
							"ISO");
					docEncoding = docEncoding.replace('-', '_');
				} else {
					docEncoding = cencoding;
				}
			} else {
				docEncoding = "";
			}
            modBit16o32 = false;
			modEncoding = false;
			bitNbrDoc = NOT_UTF;

        } else if (cencoding.equals("GB18030")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;
            modEncoding = true;
			m_utf = false;
            docEncoding = "GB18030";
		} else if (cencoding.startsWith("ISO8859")) {
			if (hasIso8859Encoding) {
				if (isoEncoding.indexOf("-") >= 0) {
					docEncoding = MiscUtil.replace(cencoding, "ISO",
							"ISO-");
					docEncoding = docEncoding.replace('_', '-');
				} else {
					docEncoding = cencoding;
				}
			} else {
				docEncoding = "";
			}
			modEncoding = false;
            modBit16o32 = false;
			bitNbrDoc = NOT_UTF;

		} else if (cencoding.startsWith("WINDOWS-12")) {
			if (hasWinEncoding) {
				if (winEncoding.indexOf("-") == -1) {
					docEncoding = MiscUtil.replace(cencoding, "WINDOWS-",
							"Cp");
				} else {
					docEncoding = cencoding;
				}
			} else {
				docEncoding = "";
			}
            modBit16o32 = false;
			modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_windows = true;
		} else if (cencoding.indexOf("CP-") == 0) {
			if (hasWinEncoding) {
				if (winEncoding.indexOf("-") >= 0) {
					docEncoding = MiscUtil.replace(cencoding, "CP-",
							"WINDOWS-");
				} else {
					docEncoding = MiscUtil.replace(cencoding, "CP-",
							"Cp");
				}
			} else {
				docEncoding = "";
			}
            modBit16o32 = false;
			modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_windows = true;
		} else if (cencoding.startsWith("CP")) {
			if (hasWinEncoding) {
				if (winEncoding.indexOf("-") >= 0) {
					docEncoding = MiscUtil.replace(cencoding, "CP",
							"WINDOWS-");
				} else {
					docEncoding = MiscUtil.replace(cencoding, "CP", "Cp");
				}
			} else {
				docEncoding = "";
			}
            modBit16o32 = false;
			modEncoding = false;
			bitNbrDoc = NOT_UTF;
			m_windows = true;
		}
		if (docEncoding.equals(fileEncoding)) {
			m_docEncoding = "";
		} else {
			m_docEncoding = docEncoding;
		}
		if (m_docEncoding.length() != 0) {
			try {
				new String("a".getBytes(), m_docEncoding);
			} catch (UnsupportedEncodingException e) {
				CauseException ce = new CauseException(
						"UnsupportedEncodingException while trying to " +
						"convert doc encoding: " + m_docEncoding, e);
				m_singleton.m_excs.addElement(ce);
				//#ifdef DLOGGING
				logger.severe(ce.getMessage(), e);
				//#endif
				System.out.println(ce.getMessage());
				// If encoding problem, use the main encoding as it is
				// close enough.
				if (m_windows) {
					if (hasWinEncoding) {
						m_docEncoding = winEncoding;
					} else {
						m_docEncoding = "";
					}
				} else if (m_utf) {
					m_docEncoding = "";
				} else {
					if (hasIso8859Encoding) {
						m_docEncoding = isoEncoding;
					} else {
						m_docEncoding = "";
					}
				}
				try {
					new String("a".getBytes(), m_docEncoding);
				} catch (UnsupportedEncodingException e2) {
					CauseException ce2 = new CauseException(
							"Second unsupportedEncodingException while " +
							" trying to convert doc encoding: " +
							m_docEncoding, e2);
					m_singleton.m_excs.addElement(ce2);
					//#ifdef DLOGGING
					logger.severe(ce2.getMessage(), e2);
					//#endif
					System.out.println(ce2.getMessage());
					m_docEncoding = "";
				}
			}
		}
		m_encodingStreamReader.setModEncoding(modEncoding);
		m_encodingStreamReader.setModBit16o32(modBit16o32);
		m_modBit16o32 = modBit16o32;
		m_encodingStreamReader.setBitNbrDoc(bitNbrDoc);
		m_bitNbrDoc = bitNbrDoc;
		m_encodingStreamReader.setGetPrologue(false);

		//#ifdef DLOGGING
        if (fineLoggable) {logger.fine("getEncoding hasIso8859Encoding=" + hasIso8859Encoding);}
        if (fineLoggable) {logger.fine("getEncoding isoEncoding=" + isoEncoding);}
        if (fineLoggable) {logger.fine("getEncoding hasWinEncoding=" + hasWinEncoding);}
        if (fineLoggable) {logger.fine("getEncoding winEncoding=" + winEncoding);}
        if (fineLoggable) {logger.fine("getEncoding encoding=" + encoding);}
        if (fineLoggable) {logger.fine("getEncoding cencoding=" + cencoding);}
        if (fineLoggable) {logger.fine("getEncoding docEncoding=" + docEncoding);}
        if (fineLoggable) {logger.fine("getEncoding m_docEncoding=" + m_docEncoding);}
        if (fineLoggable) {logger.fine("getEncoding fileEncoding=" + fileEncoding);}
        if (fineLoggable) {logger.fine("getEncoding m_windows=" + m_windows);}
        if (fineLoggable) {logger.fine("getEncoding m_utf=" + m_utf);}
        if (fineLoggable) {logger.fine("getEncoding modEncoding=" + modEncoding);}
        if (fineLoggable) {logger.fine("getEncoding m_modBit16o32=" + m_modBit16o32);}
        if (fineLoggable) {logger.fine("getEncoding m_bitNbrDoc=" + m_bitNbrDoc);}
		//#endif
    }

    public String getBitEncoding(final byte bitNbrDoc) {
		switch (bitNbrDoc) {
			case UTF_1:
				return "UTF-1";
			case UTF_7:
				return "UTF-7";
			case UTF_8:
				return "UTF-8";
			case UTF_16:
				return "UTF-16";
			case UTF_32:
				return "UTF-32";
			default:
				return null;
		}
	}

	/* Replace special characters with valid ones for the specified
	   encoding. */
	public String replaceSpChars(String text, boolean isWindows,
										boolean isUtf) {
		return replaceSpChars(text, isWindows, isUtf, m_singleton.m_sglMidpWin,
				m_singleton.m_sglMidpUni);
	}

	/* Replace special characters with valid ones for the specified
	   encoding.   For callers which use an instance of this class.  */
	public String replaceSpChars(String text) {
		return replaceSpChars(text, m_windows, m_utf, m_singleton.m_sglMidpWin,
				m_singleton.m_sglMidpUni);
	}

	/* Replace special characters with valid ones for the specified
	   encoding. */
	public String replaceSpChars(String text, final boolean isWindows,
										final boolean isUtf,
										final boolean midpWin,
										final boolean midpUni) {
		try {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("replaceSpChars isWindows,isUtf,midpWin,midpUni,text=" + isWindows + "," + isUtf + "," + midpWin + "," + midpUni + "," + text);}
			//#endif
			// No need to convert i diaeresis anymore as we do encoding
			// change.
			if (isWindows) {
				if (midpWin) {
					if (m_singleton.m_sglConvWinUni) {
						text = replaceSpUniChars(text);
						return text;
					}
				/* If we are converting a windows doc, the windows special
				   characters are control characters in other encodings,
				   so change to ASCII. */
				} else if (m_singleton.m_sglConvWinUni) {
					if (!midpUni) {
						text = replaceSpUniWinChars(text);
					}
				} else {
					char [] ctext = text.toCharArray();
					char [] ntext = new char[text.length()];
					int jc = 0;
					for (int ic = 0; ic < ctext.length; ic++) {
						final char cchr = ctext[ic];
						if ((0x80 <= (int)cchr) && ((int)cchr <= 0x9f)) {
							if (m_singleton.m_sglWinIsoConvx80[(int)cchr - 0x80] !=
									0x01) {
								ntext[jc++] = m_singleton.m_sglWinIsoConvx80[
									(int)cchr - 0x80];
								//#ifdef DTEST
								if (m_debugTrace) {System.out.println("array cchr,conv=" + cchr + "," + Integer.toHexString(cchr) + "," + ntext[jc - 1] + "," + Integer.toHexString(ntext[jc - 1]));}
								//#endif
							}
						} else {
							ntext[jc++] = cchr;
							//#ifdef DTEST
							if (m_debugTrace) {System.out.println("cchr,conv=" + cchr + "," + Integer.toHexString(cchr) + "," + ntext[jc - 1] + "," + Integer.toHexString(ntext[jc - 1]));}
							//#endif
						}
					}
					text = new String(ntext, 0, jc);
					//#ifdef DTEST
					if (m_debugTrace) {System.out.println( "text,len=" + text + "," + text.length());}
					//#endif
				}
			} else if (isUtf && !midpUni) {
				text = replaceSpUniChars(text);
			}
			text = text.replace(CNON_BREAKING_SP, ' ');
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
            logger.severe("replaceSpChars error ", t);
			//#endif
            System.out.println("replaceSpChars error " + t + "," +
					           t.getMessage());
		}
		return text;
	}

	/* Replace Unicode special characters with valid ones for Windows
	   encoding as they sometimes are valid even in iso8859_1 even though
	   it shouldn't be.  */
	public String replaceSpUniWinChars(String text) {
		//#ifdef DLOGGING
        if (traceLoggable) {logger.trace("replaceSpUniWinChars text=" + text);}
		//#endif
		try {
			final char [] ctext = text.toCharArray();
			char [] ntext = new char[text.length()];
			int jc = 0;
			for (int ic = 0; ic < ctext.length; ic++) {
				final char c = ctext[ic];
				switch(c & 0xff00) {
					case 0x2000:
						switch(c) {
							case CEN_DASH:
								ntext[jc++] = '-';
								break;
							case CEM_DASH:
								ntext[jc++] = '-';
								break;
							case CLEFT_SGL_QUOTE:
								ntext[jc++] = '\'';
								break;
							case CRIGHT_SGL_QUOTE:
								ntext[jc++] = '\'';
								break;
							case CSGL_LOW9_QUOTE:
								ntext[jc++] = '\'';
								break;
							case CLEFT_DBL_QUOTE:
								ntext[jc++] = '\"';
								break;
							case CRIGHT_DBL_QUOTE:
								ntext[jc++] = '\"';
								break;
							case CDBL_LOW9_QUOTE:
								ntext[jc++] = '\"';
								break;
							case 0x2020:
								ntext[jc++] = 0x86;
								break;
							case 0x2021:
								ntext[jc++] = 0x87;
								break;
							case 0x2022:
								ntext[jc++] = 0x95;
								break;
							case 0x2026:
								ntext[jc++] = 0x85;
								break;
							case 0x2030:
								ntext[jc++] = 0x89;
								break;
							case 0x2039:
								ntext[jc++] = 0x8B;
								break;
							case 0x203A:
								ntext[jc++] = 0x9B;
								break;
							case 0x20AC:
								ntext[jc++] = 0x80;
								System.out.println("ic,c=" + c + "," + Integer.toHexString(ntext[jc-1]));
								break;
							default:
								ntext[jc++] = c;
								break;
						}
						break;
					default:
						ntext[jc++] = c;
						break;
				}
			}
			text = new String(ntext, 0, jc);
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
            logger.severe("replaceSpUniWinChars error ", t);
			//#endif
            System.out.println("replaceSpUniWinChars error " + t + "," +
					           t.getMessage());
		}
		return text;
	}

	/* Replace Unicode special characters which have Windows (cp1252)
	   equivalents into their windows equivalents except for those
	   that have simi-equivalents (e.g. en dash to regular dash)*/
	public String replaceSpUniChars(String text) {
		//#ifdef DLOGGING
        if (traceLoggable) {logger.trace("replaceSpUniChars text=" + text);}
		//#endif
		text = text.replace(CSGL_LOW9_QUOTE, '\'');
		text = text.replace(CLEFT_SGL_QUOTE, '\'');
		text = text.replace(CRIGHT_SGL_QUOTE, '\'');
		text = text.replace(CLEFT_DBL_QUOTE, '\"');
		text = text.replace(CRIGHT_DBL_QUOTE, '\"');
		text = text.replace(CDBL_LOW9_QUOTE, '\"');
		text = text.replace(CEN_DASH, '-');
		text = text.replace(CEM_DASH, '-');
		return text;
	}

	/* Replace Windows special characters with simi-equivalents
	   (e.g. en dash to regular dash)*/
	public String replaceSpWinChars(String text) {
		//#ifdef DLOGGING
        if (traceLoggable) {logger.trace("replaceSpWinChars text=" + text);}
		//#endif
		text = text.replace(CWSGL_LOW9_QUOTE, '\'');
		text = text.replace(CWLEFT_SGL_QUOTE, '\'');
		text = text.replace(CWRIGHT_SGL_QUOTE, '\'');
		text = text.replace(CWLEFT_DBL_QUOTE, '\"');
		text = text.replace(CWRIGHT_DBL_QUOTE, '\"');
		text = text.replace(CWDBL_LOW9_QUOTE, '\"');
		text = text.replace(CWEN_DASH, '-');
		text = text.replace(CWEM_DASH, '-');
		return text;
	}

    /* Replace all numeric entites e.g. &#228;
     *   @param  s  String to alter.
     */
    public String replaceNumEntity( String s) {
		//#ifdef DLOGGING
        if (traceLoggable) {logger.trace("replaceNumEntity s=" + s);}
		//#endif
        if (s == null)  return s;
		String snum = "";
		try {
			
			int index01 = s.indexOf( "&#" );
			char [] achar = new char[1];
			while (index01 != -1) {
				int index02 = s.indexOf( ';' , index01 );
				if (index02 == -1) {
					return s;
				}
				try {
					snum = s.substring(index01 + 2, index02);
					// TODO redo with StringBuffer?
					if (snum.length() == 0) {
						return s;
					}
					switch (snum.charAt(0)) {
						case 'x':
						case 'X':
							achar[0] = (char)Integer.parseInt(snum.substring(
										1), 16);
							break;
						default:
							achar[0] = (char)Integer.parseInt(snum);
							break;
					}
					s = s.substring(0, index01) + new String(achar) +
							  s.substring(index02 + 1);
				} catch (NumberFormatException e) {
					//#ifdef DLOGGING
					Logger logger = Logger.getLogger("EncodingUtil");
					logger.severe("replaceNumEntity NumberFormatException error  for " + snum, e);
					//#endif
					System.out.println("replaceNumEntity error " + e + "," +
									   e.getMessage());
					return s;
				}
				index01 = s.indexOf( "&#" );
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
            logger.severe("replaceNumEntity error ", t);
			//#endif
            System.out.println("replaceNumEntity error " + t + "," +
					           t.getMessage());
		}
        return s;
    }
    
	/**
	  Replace alphabetic entities.
	  */
	public String replaceAlphaEntities(String text) {
		//#ifdef DLOGGING
        if (traceLoggable) {logger.trace("replaceAlphaEntities text=" + text);}
		//#endif
		final Hashtable convEntities = (m_singleton.m_sglMidpWin) ?
			m_singleton.m_sglConvCp1252 : m_singleton.m_sglConvIso88591;
		int beginPos = 0;
		int pos = -1;
		while ((pos = text.indexOf('&', beginPos)) >= 0) {
			int epos = text.indexOf(';', pos);
			if (epos < 0) {
				break;
			}
			int nbpos = text.indexOf('&', pos + 1);
			if ((nbpos >= 0) && (nbpos < epos)) {
				beginPos = nbpos;
				continue;
			}
			if ((pos + 1) == epos) {
				beginPos = epos + 1;
				continue;
			}
			String entity = text.substring(pos + 1, epos);
			if (convEntities.containsKey(entity)) {
				String ent = (String)convEntities.get(entity);
				text = text.substring(0, pos) + ent + text.substring(epos + 1);
				// If we made a substitution, keep the position the same
				// as sometimes, we get a double substitution when
				// we substitute &amp; for & this may create another
				// entity (e.g. &amp;quot; becomes & &quot;)
				beginPos = pos;
			} else {
				beginPos = epos + 1;
			}
		}
		return text;
	}

	/**
	  Create table of alpha entities for iso8859-1.
	  */
	public Hashtable initAlphaIso88591() {

		final char isoLatin1Values[] =
			{0xC0, 0xC1, 0xC2, 0xC3, 0xC4,
			0xC5, 0xC6, 0xC7, 0xC8, 0xC9,
			0xCA, 0xCB, 0xCC, 0xCD, 0xCE,
			0xCF, 0xD0, 0xD1, 0xD2, 0xD3,
			0xD4, 0xD5, 0xD6, 0xD7, 0xD8,
			0xD9, 0xDA, 0xDB, 0xDC, 0xDD,
			0xDE, 0xDF, 0xE0, 0xE1, 0xE2,
			0xE3, 0xE4, 0xE5, 0xE6, 0xE7,
			0xE8, 0xE9, 0xEA, 0xEB, 0xEC,
			0xED, 0xEE, 0xEF, 0xF0, 0xF1,
			0xF2, 0xF3, 0xF4, 0xF5, 0xF6,
			0xF7, 0xF8, 0xF9, 0xFA, 0xFB,
			0xFC, 0xFD, 0xFE, 0xFF};

		Hashtable convEntities = new Hashtable();
		try {
			initEntVals(convEntities, m_sglIsoCommonEntities, m_sglIsoCommValues);
			initEntVals(convEntities, m_sglIsoLatin1Entities, isoLatin1Values);
			initEntVals(convEntities, m_sglIsoSpecialEntities, m_sglIsoSpecialValues);
			initHtmlCommEnts(convEntities);
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initAlphaIso88591", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return convEntities;
	}

	/**
	  Create table of alpha entities for windows 1252.
	  */
	public Hashtable initAlphaCp1252() {

		char isoLatin1Values[] =
			{0xC0, 0xC1, 0xC2, 0xC3, 0xC4,
			0xC5, 0xC6, 0xC7, 0xC8, 0xC9,
			0xCA, 0xCB, 0xCC, 0xCD, 0xCE,
			0xCF, 0xD0, 0xD1, 0xD2, 0xD3,
			0xD4, 0xD5, 0xD6, 0xD7, 0xD8,
			0xD9, 0xDA, 0xDB, 0xDC, 0xDD,
			0xDE, 0xDF, 0xE0, 0xE1, 0xE2,
			0xE3, 0xE4, 0xE5, 0xE6, 0xE7,
			0xE8, 0xE9, 0xEA, 0xEB, 0xEC,
			0xED, 0xEE, 0xEF, 0xF0, 0xF1,
			0xF2, 0xF3, 0xF4, 0xF5, 0xF6,
			0xF7, 0xF8, 0xF9, 0xFA, 0xFB,
			0xFC, 0xFD, 0xFE, 0xFF};

		Hashtable convEntities = new Hashtable();
		try {
			/* ISO common entities have same encodings as Cp1252 */
			initEntVals(convEntities, m_sglIsoCommonEntities, m_sglIsoCommValues);
			initEntVals(convEntities, m_sglIsoLatin1Entities, isoLatin1Values);
			char wm_isoSpecialValues[] =
				{CWEN_DASH, // en dash 
				CWEM_DASH, // em dash 
				CWLEFT_SGL_QUOTE, // left single quotation mark 
				CWRIGHT_SGL_QUOTE, // right single quotation mark 
				0x82, // single low-9 quotation mark 
				CWLEFT_DBL_QUOTE, // left double quotation mark 
				CWRIGHT_DBL_QUOTE, // right double quotation mark 
				0x84}; // double low-9 quotation mark 
			initEntVals(convEntities, m_sglIsoSpecialEntities, wm_isoSpecialValues);
			initHtmlCommEnts(convEntities);
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initAlphaCp1252", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return convEntities;
	}

	/* Initialize entries with passed in entity strings and character
	   values turned into strings. */
	public void initEntVals(Hashtable convEntities, String[] entities, char[] entValues) {
		try {
			//#ifdef DTEST
			System.out.println( "Entities, values len=" + entities.length + "," + entValues.length);
			//#endif
			for (int ic = 0; (ic < entities.length) && (ic < entValues.length);
					ic++) {
				char [] cvalue = {entValues[ic]};
				// Sometimes, this can produce an error in some default
				// encodings.
				try {
					String value = new String(cvalue);
					convEntities.put(entities[ic], value);
				} catch (Throwable t) {
					//#ifdef DLOGGING
					Logger logger = Logger.getLogger("EncodingUtil");
					logger.severe("initEntVals convert error bvalue=" +
							Integer.toHexString(cvalue[0]), t);
					//#else
					t.printStackTrace();
					//#endif
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initEntVals", t);
			//#else
			t.printStackTrace();
			//#endif
		}
	}

	/* Init windows (cp-1252) to Iso 8859 encoding.  This has either 1
	   if there is no equivalent (this is used to remove the equivalent char
	   from the string to be converted).  If not a 1, the character is
	   used to replace the character in the string to be converted.
	   The conversion starts at 0x80 and goes to including 0x9f.
	   */
	private char [] initWinIsoConv() {
		char [] convTable = new char[0x9f - 0x80 + 1];
		try {
			//#ifdef DTEST
			System.out.println( "convTable.length=" + convTable.length);
			//#endif
			convTable[0x80 - 0x80] = 0x20AC; //EURO SIGN
			convTable[0x81 - 0x80] = 0x01;
			convTable[0x82 - 0x80] = '\''; //SINGLE LOW-9 QUOTATION MARK
			convTable[0x83 - 0x80] = 0x0192; //LATIN SMALL LETTER F WITH HOOK
			convTable[0x84 - 0x80] = '\"'; //DOUBLE LOW-9 QUOTATION MARK
			convTable[0x85 - 0x80] = 0x2026; //HORIZONTAL ELLIPSIS
			convTable[0x86 - 0x80] = 0x2020; //DAGGER
			convTable[0x87 - 0x80] = 0x2021; //DOUBLE DAGGER
			convTable[0x88 - 0x80] = 0x02C6; //MODIFIER LETTER CIRCUMFLEX ACCENT
			convTable[0x89 - 0x80] = 0x2030; //PER MILLE SIGN
			convTable[0x8A - 0x80] = 0x0160; //LATIN CAPITAL LETTER S WITH CARON
			convTable[0x8B - 0x80] = 0x2039; //SINGLE LEFT-POINTING ANGLE QUOTATION MARK
			convTable[0x8C - 0x80] = 0x0152; //LATIN CAPITAL LIGATURE OE
			convTable[0x8D - 0x80] = 0x01;
			convTable[0x8E - 0x80] = 0x017D; //LATIN CAPITAL LETTER Z WITH CARON
			convTable[0x8F - 0x80] = 0x01;
			convTable[0x90 - 0x80] = 0x01;
			convTable[0x91 - 0x80] = '\''; //LEFT SINGLE QUOTATION MARK
			convTable[0x92 - 0x80] = '\''; //RIGHT SINGLE QUOTATION MARK
			convTable[0x93 - 0x80] = '\"'; //LEFT DOUBLE QUOTATION MARK
			convTable[0x94 - 0x80] = '\"'; //RIGHT DOUBLE QUOTATION MARK
			convTable[0x95 - 0x80] = 0x2022; //BULLET
			convTable[0x96 - 0x80] = '-'; //EN DASH
			convTable[0x97 - 0x80] = '-'; //EM DASH
			convTable[0x98 - 0x80] = 0x02DC; //SMALL TILDE
			convTable[0x99 - 0x80] = 0x2122; //TRADE MARK SIGN
			convTable[0x9A - 0x80] = 0x0161; //LATIN SMALL LETTER S WITH CARON
			convTable[0x9B - 0x80] = 0x203A; //SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
			convTable[0x9C - 0x80] = 0x0153; //LATIN SMALL LIGATURE OE
			convTable[0x9D - 0x80] = 0x01;
			convTable[0x9E - 0x80] = 0x017E; //LATIN SMALL LETTER Z WITH CARON
			convTable[0x9F - 0x80] = 0x0178; //LATIN CAPITAL LETTER Y WITH DIAERESIS
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initWinIsoConv", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return convTable;
	}

	/* Init unicode to windows (cp-1252).  This has either 1
	   if there is no equivalent (this is used to remove the equivalent char
	   from the string to be converted).  If not a 1, the character is
	   used to replace the character in the string to be converted.
	   The conversion starts at 0x80 and goes to including 0x9f.
	   */
	private char [] initUniWinConvx80() {
		char [] convTable = new char[0x9f - 0x80 + 1];
		try {
			//#ifdef DTEST
			System.out.println( "convTable.length=" + convTable.length);
			//#endif
			for (int ic = 0; ic < convTable.length; ic++) {
				char cc = (char)(ic + 0x80);
				switch (cc) {
					case CWSGL_LOW9_QUOTE:
						convTable[ic] = '\'';
						break;
					case CWDBL_LOW9_QUOTE:
						convTable[ic] = '\"';
						break;
					case CWLEFT_DBL_QUOTE:
						convTable[ic] = '\"';
						break;
					case CWRIGHT_DBL_QUOTE:
						convTable[ic] = '\"';
						break;
					case CWLEFT_SGL_QUOTE:
						convTable[ic] = '\'';
						break;
					case CWEN_DASH:
						convTable[ic] = '-';
						break;
					case CWEM_DASH:
						convTable[ic] = '-';
						break;
					default:
						convTable[ic] = 0x01;
						break;
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initUniWinConvx80", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return convTable;
	}

	/* Initialize entries for XML. */
	private void initHtmlCommEnts(Hashtable convEntities) {
		String htmlCommonEntities[] =
				{"lt", "gt", "nbsp", "amp", "apos", "quot"};
		char htmlCommonValues[] = {'<', '>', ' ', '&', '\'', '\"'};
		initEntVals(convEntities, htmlCommonEntities, htmlCommonValues);
	}

	/* Determine if creating a string converts the windows chars to
	   Unicode. */
	private boolean initConvWinUni() {
		boolean rtn = false;
		try {
			byte[] blftSgl = {(byte)CWLEFT_SGL_QUOTE};
			try {
				String convStr = new String(blftSgl, "Cp1252");
				rtn = convStr.charAt(0) == CLEFT_SGL_QUOTE;
			} catch (UnsupportedEncodingException e) {
				//#ifdef DTEST
				System.out.println( "Unsupported encoding Cp1252");
				//#endif
				//#ifdef DLOGGING
				Logger logger = Logger.getLogger("EncodingUtil");
				logger.severe("UnsupportedEncodingException Cp1252", e);
				//#endif
				try {
					String convStr2 = new String(blftSgl, "Cp1252");
					rtn = convStr2.charAt(0) == CLEFT_SGL_QUOTE;
				} catch (UnsupportedEncodingException e2) {
					//#ifdef DTEST
					System.out.println( "Unsupported encoding WINDOWS-1252");
					//#endif
					//#ifdef DLOGGING
					logger.severe("UnsupportedEncodingException Cp1252", e2);
					//#else
					e2.printStackTrace();
					//#endif
				}
			}
			//#ifdef DTEST
			System.out.println( "initConvWinUni()=" + rtn);
			//#endif
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initConvWinUni", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return rtn;
	}

	/* Determine ISO encoding string. */
	private String initIsoEncoding() {
		try {
			try {
				new String("a".getBytes(), "ISO8859_1");
				return "ISO8859_1";
			} catch (UnsupportedEncodingException e) {
				//#ifdef DTEST
				System.out.println( "Unsupported encoding ISO8859_1");
				//#endif
				//#ifdef DLOGGING
				Logger logger = Logger.getLogger("EncodingUtil");
				logger.severe("initIsoEncoding UnsupportedEncodingException ISO8859_1", e);
				//#endif
				try {
					new String("a".getBytes(), "ISO-8859-1");
					return "ISO-8859-1";
				} catch (UnsupportedEncodingException e2) {
					//#ifdef DTEST
					System.out.println("initIsoEncoding Unsupported encoding ISO-8859-1");
					//#endif
					//#ifdef DLOGGING
					logger.severe("initIsoEncoding UnsupportedEncodingException ISO-8859-1", e2);
					//#else
					e2.printStackTrace();
					//#endif
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initIsoEncoding initConvWinUni", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return "ISO8859_1";
	}

	/* Determine Windows encoding string. */
	private String initWinEncoding() {
		try {
			try {
				new String("a".getBytes(), "Cp1252");
				return "Cp1252";
			} catch (UnsupportedEncodingException e) {
				CauseException ce = new CauseException(
						"initWinEncoding UnsupportedEncodingException " +
						"while trying to convert encoding Cp1252.", e);
				m_sglStatExcs.addElement(ce);
				//#ifdef DTEST
				System.out.println(ce.getMessage());
				//#endif
				//#ifdef DLOGGING
				Logger logger = Logger.getLogger("EncodingUtil");
				logger.severe(ce.getMessage(), e);
				//#endif
				try {
					new String("a".getBytes(), "WINDOWS-1252");
					return "WINDOWS-1252";
				} catch (UnsupportedEncodingException e2) {
					CauseException ce2 = new CauseException(
							"initWinEncoding second " +
							"unsupportedEncodingException while " +
							" trying to convert encoding WINDOWS-1252.", e2);
					m_sglStatExcs.addElement(ce2);
					//#ifdef DTEST
					System.out.println(ce2.getMessage());
					//#endif
					//#ifdef DLOGGING
					logger.severe(ce2.getMessage(), e2);
					//#endif
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initWinEncoding() initConvWinUni", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return "Cp1252";
	}

	/* Determine if windows encoding is supported.  */
	public boolean initHasWinEncoding() {
		try {
			try {
				new String("a".getBytes(), "Cp1252");
				return true;
			} catch (UnsupportedEncodingException e) {
				CauseException ce = new CauseException(
						"hasWinEncoding UnsupportedEncodingException " +
						"while trying to convert encoding Cp1252.", e);
				m_sglStatExcs.addElement(ce);
				//#ifdef DTEST
				System.out.println(ce.getMessage());
				//#endif
				//#ifdef DLOGGING
				Logger logger = Logger.getLogger("EncodingUtil");
				logger.severe(ce.getMessage(), e);
				//#endif
				try {
					new String("a".getBytes(), "WINDOWS-1252");
					return true;
				} catch (UnsupportedEncodingException e2) {
					CauseException ce2 = new CauseException(
							"initWinEncoding second " +
							"unsupportedEncodingException while " +
							" trying to convert encoding WINDOWS-1252.", e2);
					m_sglStatExcs.addElement(ce2);
					//#ifdef DTEST
					System.out.println(ce2.getMessage());
					//#endif
					//#ifdef DLOGGING
					logger.severe(ce2.getMessage(), e2);
					//#endif
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("hasWinEncoding initConvWinUni", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return false;
	}

	/* Determine if iso-8859-1 encoding is supported.  */
	public boolean initHasIso8859Encoding() {
		try {
			try {
				new String("a".getBytes(), "ISO8859_1");
				return true;
			} catch (UnsupportedEncodingException e) {
				//#ifdef DTEST
				System.out.println( "Unsupported encoding ISO8859_1");
				//#endif
				//#ifdef DLOGGING
				Logger logger = Logger.getLogger("EncodingUtil");
				logger.severe("initHasIso8859Encoding UnsupportedEncodingException ISO8859_1", e);
				//#endif
				try {
					new String("a".getBytes(), "ISO-8859-1");
					return true;
				} catch (UnsupportedEncodingException e2) {
					//#ifdef DTEST
					System.out.println("initHasIso8859Encoding Unsupported encoding ISO-8859-1");
					//#endif
					//#ifdef DLOGGING
					logger.severe("initIsoEncoding UnsupportedEncodingException ISO-8859-1", e2);
					//#else
					e2.printStackTrace();
					//#endif
				}
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("EncodingUtil");
			logger.severe("initHasIso8859Encoding initConvWinUni", t);
			//#else
			t.printStackTrace();
			//#endif
		}
		return false;
	}

    public void setDocEncoding(String docEncoding) {
        this.m_docEncoding = docEncoding;
    }

    public String getDocEncoding() {
        return (m_docEncoding);
    }

    public void setEncodingStreamReader(EncodingStreamReader encodingStreamReader) {
        this.m_encodingStreamReader = encodingStreamReader;
    }

    public EncodingStreamReader getEncodingStreamReader() {
        return (m_encodingStreamReader);
    }

    public boolean isWindows() {
        return (m_windows);
    }

    public boolean isUtf() {
        return (m_utf);
    }

    public void setBitNbrDoc(byte bitNbrDoc) {
        this.m_bitNbrDoc = bitNbrDoc;
    }

    public byte getBitNbrDoc() {
        return (m_bitNbrDoc);
    }

	//#ifdef DTEST
    public String[] getIsoCommonEntities() {
        return (m_singleton.m_sglIsoCommonEntities);
    }

    public Hashtable getConvIso88591() {
        return (m_sglConvIso88591);
    }

    public Hashtable getConvCp1252() {
        return (m_sglConvCp1252);
    }

    public String[] getIsoSpecialEntities() {
        return (m_singleton.m_sglIsoSpecialEntities);
    }

    public char[] getIsoSpecialValues() {
        return (m_sglIsoSpecialValues);
    }

    public char[] getIsoCommValues() {
        return (m_sglIsoCommValues);
    }

    public String[] getIsoLatin1Entities() {
        return (m_sglIsoLatin1Entities);
    }

    public String getWinEncoding() {
        return (m_singleton.m_sglwinEncoding);
    }

    public boolean isConvWinUni() {
        return (m_singleton.m_sglConvWinUni);
    }

    public boolean isHasWinEncoding() {
        return (m_singleton.m_sglHasWinEncoding);
    }

	//#endif

    public String getIsoEncoding() {
        return (m_singleton.m_sglIsoEncoding);
    }

    static public Vector getExcs() {
		if (m_singleton.m_excs == null) {
			return new Vector();
		} else {
			return (m_singleton.m_excs);
		}
    }

    public Vector getStatExcs() {
		if (m_singleton.m_sglStatExcs == null) {
			return new Vector();
		} else {
			return (m_singleton.m_sglStatExcs);
		}
    }

}
//#endif
