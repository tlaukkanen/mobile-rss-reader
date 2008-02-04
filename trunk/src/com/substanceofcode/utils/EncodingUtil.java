 /*
 * EncodingUtil.java
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

// Expand to define test define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
public class EncodingUtil {
    
	final static public boolean m_midpIso = System.getProperty(
			"microedition.encoding").toLowerCase().startsWith("iso8859");
	final static public boolean m_midpWin = System.getProperty(
			"microedition.encoding").toLowerCase().startsWith("cp");
	final static public boolean m_midpUni = System.getProperty(
			"microedition.encoding").toLowerCase().startsWith("utf-8");
	final static String[] m_isoCommonEntities =
		{"iexcl", "cent", "pound", "curren", "yen",
		"brvbar", "sect", "uml", "copy", "ordf",
		"laquo", "not", "shy", "reg", "macr",
		"deg", "plusmn", "sup2", "sup3", "acute",
		"micro", "para", "middot", "cedil", "sup1",
		"ordm", "raquo", "frac14", "frac12", "frac34",
		"iquest"};

	final static String[] m_isoSpecialEntities =
			{"ndash", // en dash 
			"mdash", // em dash 
			"lsquo", // left single quotation mark 
			"rsquo", // right single quotation mark 
			"sbquo", // single low-9 quotation mark 
			"ldquo", // left double quotation mark 
			"rdquo", // right double quotation mark 
			"bdquo"}; // double low-9 quotation mark 

	final static char[] m_isoSpecialValues =
			{'-', // en dash 
			'-', // em dash 
			'\'', // left single quotation mark 
			'\'', // right single quotation mark 
			'\'', // single low-9 quotation mark 
			'\"', // left double quotation mark 
			'\"', // right double quotation mark 
			'\"'}; // double low-9 quotation mark 

	final static char[] m_isoCommValues = 
		{0xA1, 0xA2, 0xA3, 0xA4, 0xA5,
		0xA6, 0xA7, 0xA8, 0xA9, 0xAA,
		0xAB, 0xAC, 0xAD, 0xAE, 0xAF,
		0xB0, 0xB1, 0xB2, 0xB3, 0xB4,
		0xB5, 0xB6, 0xB7, 0xB8, 0xB9,
		0xBA, 0xBB, 0xBC, 0xBD, 0xBE,
		0xBF};

	final static String[] m_isoLatin1Entities = 
		{"Agrave", "Aacute", "Acirc", "Atilde", "Auml",
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
	public static char[] m_winIsoConvx80 = initWinIsoConv();

	// Left single quote in cp-1252 (Windows) encoding.
    public static final char CWSGL_LOW9_QUOTE = 0x82; // #130;
    private static final char [] CAWSGL_LOW9_QUOTE = {CWSGL_LOW9_QUOTE};
    public static final String WSGL_LOW9_QUOTE = new String(CAWSGL_LOW9_QUOTE);
    public static final char CWDBL_LOW9_QUOTE = 0x84; // #132;
    private static final char [] CAWDBL_LOW9_QUOTE = {CWDBL_LOW9_QUOTE};
    public static final String WDBL_LOW9_QUOTE = new String(CAWDBL_LOW9_QUOTE);
    public static final char CWLEFT_SGL_QUOTE = 0x91; // #145;
    private static final char [] CAWLEFT_SGL_QUOTE = {CWLEFT_SGL_QUOTE};
    public static final String WLEFT_SGL_QUOTE = new String(CAWLEFT_SGL_QUOTE);
    public static final char CWRIGHT_SGL_QUOTE = 0x92; // #146;
    private static final char [] CAWRIGHT_SGL_QUOTE = {CWRIGHT_SGL_QUOTE};
    public static final String WRIGHT_SGL_QUOTE = new String(CAWRIGHT_SGL_QUOTE);
    public static final char CWLEFT_DBL_QUOTE = 0x93; // #147;
    private static final char [] CAWLEFT_DBL_QUOTE = {CWLEFT_DBL_QUOTE};
    public static final String WLEFT_DBL_QUOTE = new String(CAWLEFT_DBL_QUOTE);
    public static final char CWRIGHT_DBL_QUOTE = 0x94; // #148;
    private static final char [] CAWRIGHT_DBL_QUOTE = {CWRIGHT_DBL_QUOTE};
    public static final String WRIGHT_DBL_QUOTE = new String(CAWRIGHT_DBL_QUOTE);
    public static final char CWEN_DASH = 0x96; // #150;
    private static final char [] CAWEN_DASH = {CWEN_DASH};
    public static String WEN_DASH = new String(CAWEN_DASH);
    public static final char CWEM_DASH = 0x97; // #151;
    private static final char [] CAWEM_DASH = {CWEM_DASH};
    public static String WEM_DASH = new String(CAWEM_DASH);
	// A.k.a CYRILLIC SMALL LETTER DE
    public static final String [] WCONV_CHARS = {WLEFT_SGL_QUOTE,
													WRIGHT_SGL_QUOTE,
													WLEFT_DBL_QUOTE,
													WRIGHT_DBL_QUOTE,
													WEN_DASH};
	// Left single quote in Unicode (utf-16) encoding.
	// Long dash a.k.a en dash
    public static final char CEN_DASH = 0x2013;
    private static final char [] CAEN_DASH = {CEN_DASH};
    public static String EN_DASH = new String(CAEN_DASH);
    public static final char CEM_DASH = 0x2014;
    private static final char [] CAEM_DASH = {CEM_DASH};
    public static String EM_DASH = new String(CAEM_DASH);
    public static final char CLEFT_SGL_QUOTE = 0x2018;
    private static final char [] CALEFT_SGL_QUOTE = {CLEFT_SGL_QUOTE};
    public static final String LEFT_SGL_QUOTE = new String(CALEFT_SGL_QUOTE);
    public static final char CRIGHT_SGL_QUOTE = 0x2019;
    private static final char [] CARIGHT_SGL_QUOTE = {CRIGHT_SGL_QUOTE};
    public static final String RIGHT_SGL_QUOTE = new String(CARIGHT_SGL_QUOTE);
    public static final char CSGL_LOW9_QUOTE = 0x201A;
    private static final char [] CASGL_LOW9_QUOTE = {CSGL_LOW9_QUOTE};
    public static final String SGL_LOW9_QUOTE = new String(CASGL_LOW9_QUOTE);
    private static final char CLEFT_DBL_QUOTE = 0x201C;
    private static final char [] CALEFT_DBL_QUOTE = {CLEFT_DBL_QUOTE};
    public static String LEFT_DBL_QUOTE = new String(CALEFT_DBL_QUOTE);
    private static final char CRIGHT_DBL_QUOTE = 0x201D;
    private static final char [] CARIGHT_DBL_QUOTE = {CRIGHT_DBL_QUOTE};
    public static String RIGHT_DBL_QUOTE = new String(CARIGHT_DBL_QUOTE);
    public static final char CDBL_LOW9_QUOTE = 0x201E;
    private static final char [] CADBL_LOW9_QUOTE = {CDBL_LOW9_QUOTE};
    public static final String DBL_LOW9_QUOTE = new String(CADBL_LOW9_QUOTE);
    public static final char CA_UMLAUTE = (char)228;
    private static final char [] CAA_UMLAUTE = {CA_UMLAUTE};
    public static String A_UMLAUTE = new String(CAA_UMLAUTE);
    private static final char CO_UMLAUTE = (char)246;
    private static final char [] CAO_UMLAUTE = {CO_UMLAUTE};
    public static String O_UMLAUTE = new String(CAO_UMLAUTE);
    public static final String [] CONV_CHARS = {LEFT_SGL_QUOTE,
												RIGHT_SGL_QUOTE,
												LEFT_DBL_QUOTE,
												RIGHT_DBL_QUOTE,
												EN_DASH};

    public static final char CNON_BREAKING_SP = (char)160;
    private static final char [] CANON_BREAKING_SP = {CNON_BREAKING_SP};
    public static String NON_BREAKING_SP = new String(CANON_BREAKING_SP);
    private static final char CAPOSTROPHY = (char)39;
    private static final char [] CAAPOSTROPHY = {CAPOSTROPHY};
    public static String APOSTROPHY = new String(CAAPOSTROPHY);
    
    private EncodingStreamReader m_encodingStreamReader;
	private static Hashtable m_convIso88591 = initAlphaIso88591();
	private static Hashtable m_convCp1252 = initAlphaCp1252();
    private String m_docEncoding = "";  // Default for XML is UTF-8.
	                                    // unexpected UTF-16.
    private boolean m_utf = false;  // Doc is utf.
    private boolean m_getPrologue = true;
    private boolean m_windows = false;  // True if windows code space
	private static boolean m_convWinUni = initConvWinUni();

	//#ifdef DTEST
//@    private static boolean m_debugTrace = true;  // True if want to trace more
	//#endif
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("EncodingUtil");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of EncodingUtil */
    public EncodingUtil(InputStream inputStream) {
		m_encodingStreamReader = new EncodingStreamReader(inputStream);
    }

	/**  Determine the encoding based on what is passed in as well
	  as if/when strings are to be further encoded.  Also decide to
	  modify bytes read.  
	 **/

    public void getEncoding(String encoding) {
		String cencoding = encoding;
        // If there is a second char, don't stop splitting until we
        // return that char as input.
        if (cencoding == null) {
           cencoding = "UTF-8";
        }
        cencoding = cencoding.toUpperCase();
		boolean modUTF16 = m_encodingStreamReader.isModUTF16();
		boolean modEncoding = m_encodingStreamReader.isModEncoding();
		m_utf = false;
		// Only need to convert from 2 byte to 1 byte and vsa versa.
        if ((cencoding.equals("UTF-8") || cencoding.equals("UTF8"))) {
            m_docEncoding = "UTF-8";
            modEncoding = false;
			m_utf = true;
        } else if (cencoding.equals("UTF-16") || cencoding.equals("UTF16")) {
			// If utf-16, don't set doc encoding as we are converting the
			// bytes to single chars.
            modUTF16 = true;
			m_utf = true;
		} else if (cencoding.startsWith("ISO-8859")) {
			m_docEncoding = StringUtil.replace(cencoding, "ISO-",
					"ISO");

			m_docEncoding = m_docEncoding.replace('-', '_');
			if (m_docEncoding.equals("ISO8859_1")) {
				m_docEncoding = "";
			}
			modEncoding = false;
		} else if (cencoding.indexOf("WINDOWS-12") == 0) {
			m_docEncoding = StringUtil.replace(cencoding, "WINDOWS-", "Cp");
			modEncoding = false;
			m_windows = true;
		} else if (cencoding.indexOf("CP-") == 0) {
			m_docEncoding = StringUtil.replace(cencoding, "CP-", "Cp");
			modEncoding = false;
			m_windows = true;
		}
		if (m_docEncoding.length() != 0) {
			try {
				String a = new String("a".getBytes(), m_docEncoding);
			} catch (UnsupportedEncodingException e) {
				//#ifdef DLOGGING
//@				logger.severe("UnsupportedEncodingException error for " +
//@							   "encoding: " + m_docEncoding);
				//#endif
				System.out.println("UnsupportedEncodingException error for " +
							   "encoding: " + m_docEncoding + " " + e + " " +
							   e.getMessage());
				// If encoding problem, use the main encoding as it is
				// close enough.
				if (m_docEncoding.indexOf("ISO8859") >= 0) {
					m_docEncoding = "ISO8859_1";
				} else if (m_docEncoding.indexOf("Cp12") >= 0) {
					m_docEncoding = "Cp1252";
				}
				try {
					String a = new String("a".getBytes(), m_docEncoding);
				} catch (UnsupportedEncodingException e2) {
					//#ifdef DLOGGING
//@					logger.severe("UnsupportedEncodingException error for " +
//@								   "encoding: " + m_docEncoding);
					//#endif
					System.out.println("UnsupportedEncodingException error " +
							           "for encoding: " + m_docEncoding + " "
									   + e + " " + e.getMessage());
					m_docEncoding = "";
				}
			}
		}
		m_encodingStreamReader.setModEncoding(modEncoding);
		m_encodingStreamReader.setModUTF16(modUTF16);

		//#ifdef DLOGGING
//@        if (fineLoggable) {logger.fine("encoding=" + encoding);}
//@        if (fineLoggable) {logger.fine("cencoding=" + cencoding);}
//@        if (fineLoggable) {logger.fine("m_docEncoding=" + m_docEncoding);}
//@        if (fineLoggable) {logger.fine("m_windows=" + m_windows);}
//@        if (fineLoggable) {logger.fine("m_utf=" + m_utf);}
//@        if (fineLoggable) {logger.fine("modEncoding=" + modEncoding);}
//@        if (fineLoggable) {logger.fine("modUTF16=" + modUTF16);}
		//#endif
    }

	/* Replace special characters with valid ones for the specified
	   encoding. */
	public static String replaceSpChars(String text, boolean isWindows,
										boolean isUtf) {
		return replaceSpChars(text, isWindows, isUtf, m_midpWin, m_midpUni);
	}

	/* Replace special characters with valid ones for the specified
	   encoding.   For callers which use an instance of this class.  */
	public String replaceSpChars(String text) {
		return replaceSpChars(text, m_windows, m_utf, m_midpWin, m_midpUni);
	}

	/* Replace special characters with valid ones for the specified
	   encoding. */
	public static String replaceSpChars(String text, boolean isWindows,
										boolean isUtf,
										boolean midpWin, boolean midpUni) {
		try {
			// No need to convert i diaeresis anymore as we do encoding
			// change.
			if (isWindows) {
				if (midpWin) {
					if (m_convWinUni) {
						text = replaceSpUniChars(text);
						return text;
					}
				/* If we are converting a windows doc, the windows special
				   characters are control characters in other encodings,
				   so change to ASCII. */
				} else if (m_convWinUni) {
					text = replaceSpUniChars(text);
				} else {
					char [] ctext = text.toCharArray();
					char [] ntext = new char[text.length()];
					int jc = 0;
					for (int ic = 0; ic < ctext.length; ic++) {
						final char cchr = ctext[ic];
						if ((0x80 <= (int)cchr) && ((int)cchr <= 0x9f)) {
							if (m_winIsoConvx80[(int)cchr - 0x80] != 0x01) {
								ntext[jc++] = m_winIsoConvx80[(int)cchr - 0x80];
								//#ifdef DTEST
//@								if (m_debugTrace) {System.out.println("array cchr,conv=" + cchr + "," + Integer.toHexString(cchr) + "," + ntext[jc - 1] + "," + Integer.toHexString(ntext[jc - 1]));}
								//#endif
							}
						} else {
							ntext[jc++] = cchr;
							//#ifdef DTEST
//@							if (m_debugTrace) {System.out.println("cchr,conv=" + cchr + "," + Integer.toHexString(cchr) + "," + ntext[jc - 1] + "," + Integer.toHexString(ntext[jc - 1]));}
							//#endif
						}
					}
					text = new String(ntext, 0, jc);
					//#ifdef DTEST
//@					if (m_debugTrace) {System.out.println( "text,len=" + text + "," + text.length());}
					//#endif
				}
			} else if (isUtf && !midpUni) {
				text = replaceSpUniChars(text);
			}
			text = text.replace(CNON_BREAKING_SP, ' ');
		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("EncodingUtil");
//@            logger.severe("replaceSpChars error ", t);
			//#endif
            System.out.println("replaceSpChars error " + t + "," +
					           t.getMessage());
		}
		return text;
	}

	/* Replace Unicode special characters with valid ones for all encodings */
	public static String replaceSpUniChars(String text) {
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

    /* Replace all numeric entites e.g. &#228;
     *   @param  s  String to alter.
     */
    public static String replaceNumEntity( String s) {
        if (s == null)  return s;
		try {
			
			int index01 = s.indexOf( "&#" );
			char [] achar = new char[1];
			while (index01 != -1) {
				int index02 = s.indexOf( ';' , index01 );
				if (index02 == -1) {
					return s;
				}
				try {
					String snum = s.substring(index01 + 2, index02);
					// TODO redo with StringBuffer?
					if (snum.length() == 0) {
						return s;
					}
					switch (snum.charAt(0)) {
						case 'x':
						case 'X':
							achar[0] = (char)Integer.parseInt(snum, 16);
						default:
							achar[0] = (char)Integer.parseInt(snum);
							break;
					}
					s = s.substring(0, index01) + new String(achar) +
							  s.substring(index02 + 1);
				} catch (NumberFormatException e) {
					return s;
				}
				index01 = s.indexOf( "&#" );
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("EncodingUtil");
//@            logger.severe("replaceNumEntity error ", t);
			//#endif
            System.out.println("replaceNumEntity error " + t + "," +
					           t.getMessage());
		}
        return s;
    }
    
	/**
	  Replace alphabetic entities.
	  */
	public static String replaceAlphaEntities(String text) {
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
			Hashtable m_convEntities = (m_midpWin) ? m_convCp1252 :
					m_convIso88591;
			if (m_convEntities.containsKey(entity)) {
				String ent = (String)m_convEntities.get(entity);
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
	public static Hashtable initAlphaIso88591() {

		//#ifdef DTEST
//@		System.out.println( "m_midpIso=" + m_midpIso);
		//#endif
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
		initEntVals(convEntities, m_isoCommonEntities, m_isoCommValues);
		initEntVals(convEntities, m_isoLatin1Entities, isoLatin1Values);
		initEntVals(convEntities, m_isoSpecialEntities, m_isoSpecialValues);
		initHtmlCommEnts(convEntities);
		return convEntities;
	}

	/**
	  Create table of alpha entities for windows 1252.
	  */
	public static Hashtable initAlphaCp1252() {

		//#ifdef DTEST
//@		System.out.println( "m_midpWin=" + m_midpWin);
		//#endif
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
		/* ISO common entities have same encodings as Cp1252 */
		initEntVals(convEntities, m_isoCommonEntities, m_isoCommValues);
		initEntVals(convEntities, m_isoLatin1Entities, isoLatin1Values);
		char wm_isoSpecialValues[] =
			{CWEN_DASH, // en dash 
			CWEM_DASH, // em dash 
			CWLEFT_SGL_QUOTE, // left single quotation mark 
			CWRIGHT_SGL_QUOTE, // right single quotation mark 
			0x82, // single low-9 quotation mark 
			CWLEFT_DBL_QUOTE, // left double quotation mark 
			CWRIGHT_DBL_QUOTE, // right double quotation mark 
			0x84}; // double low-9 quotation mark 
		initEntVals(convEntities, m_isoSpecialEntities, wm_isoSpecialValues);
		initHtmlCommEnts(convEntities);
		return convEntities;
	}

	/* Initialize entries with passed in entity strings and character
	   values turned into strings. */
	public static void initEntVals(Hashtable convEntities, String[] entities, char[] entValues) {
		//#ifdef DTEST
//@		System.out.println( "Entities, values len=" + entities.length + "," + entValues.length);
		//#endif
		for (int ic = 0; (ic < entities.length) && (ic < entValues.length);
				ic++) {
			char [] cvalue = new char [1];
			byte [] bvalue = {(byte)entValues[ic]};
			String value = new String(bvalue);
			convEntities.put(entities[ic], value);
		}
	}

	/* Init windows (cp-1252) to Iso 8859 encoding.  This has either 1
	   if there is no equivalent (this is used to remove the equivalent char
	   from the string to be converted).  If not a 1, the character is
	   used to replace the character in the string to be converted.
	   The conversion starts at 0x80 and goes to including 0x9f.
	   */
	private static char [] initWinIsoConv() {
		char [] convTable = new char[0x9f - 0x80 + 1];
		//#ifdef DTEST
//@		System.out.println( "convTable.length=" + convTable.length);
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
		return convTable;
	}

	/* Initialize entries for XML. */
	private static void initHtmlCommEnts(Hashtable convEntities) {
		String htmlCommonEntities[] =
				{"lt", "gt", "nbsp", "amp", "apos", "quot"};
		char htmlCommonValues[] = {'<', '>', ' ', '&', '\'', '\"'};
		initEntVals(convEntities, htmlCommonEntities, htmlCommonValues);
	}

	/* Determine if creating a string converts the windows chars to
	   Unicode. */
	private static boolean initConvWinUni() {
		boolean rtn = false;
		byte[] blftSgl = {(byte)CWLEFT_SGL_QUOTE};
		try {
			String convStr = new String(blftSgl, "Cp1252");
			rtn = convStr.charAt(0) == CLEFT_SGL_QUOTE;
		} catch (UnsupportedEncodingException e) {
			//#ifdef DTEST
//@			System.out.println( "Unsupported encoding Cp1252");
			//#endif
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("EncodingUtil");
//@			logger.severe("UnsupportedEncodingException Cp1252", e);
			//#endif
		}
		//#ifdef DTEST
//@		System.out.println( "initConvWinUni()=" + rtn);
		//#endif
		return rtn;
	}

    public void setDocEncoding(String m_docEncoding) {
        this.m_docEncoding = m_docEncoding;
    }

    public String getDocEncoding() {
        return (m_docEncoding);
    }

    public void setEncodingStreamReader(EncodingStreamReader m_encodingStreamReader) {
        this.m_encodingStreamReader = m_encodingStreamReader;
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

	//#ifdef DTEST
//@    public static String[] getIsoCommonEntities() {
//@        return (m_isoCommonEntities);
//@    }
//@
//@    public static Hashtable getConvIso88591() {
//@        return (m_convIso88591);
//@    }
//@
//@    public static Hashtable getConvCp1252() {
//@        return (m_convCp1252);
//@    }
//@
//@    static public String[] getIsoSpecialEntities() {
//@        return (m_isoSpecialEntities);
//@    }
//@
//@    public static boolean isConvWinUni() {
//@        return (m_convWinUni);
//@    }
	//#endif

}
